**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 17/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Traducir el flujo de negocio de DOM-002 - Event Storming en Casos de Uso concretos: qué dispara cada uno, qué agregado (DOM-006) modifica, y qué evento de dominio (DOM-007) produce. Esta es la base directa para los `UseCase` de la capa `application` definida en ADR-004.

## Contexto

No todos los comandos de DOM-002 los dispara el Trader esperando una respuesta síncrona. Varios son reacciones internas a eventos ya ocurridos (por ejemplo, `AnalyzeMarket` no lo pide nadie: ocurre automáticamente cuando llega `MarketDataReceived`). Por eso este documento separa:

- **Casos de Uso** — el Trader los invoca vía API REST y espera una respuesta.
- **Procesos reactivos** — se disparan al reaccionar a un evento de dominio o integración; no hay un actor humano esperando en línea.

Ambos se implementan igual en código (como Use Cases en `application`), pero cambia quién los invoca: un Controller REST en el primer caso, un Event Listener en el segundo.

---

# 1. Casos de Uso (invocados por el Trader)

## UC-01 Crear Strategy

- Módulo: `strategy`.
- Actor: Trader.
- Precondición: ninguna.
- Flujo principal: el Trader envía nombre, `tradingPair`, `timeframe` y reglas de entrada/salida → se crea la Strategy en estado `CREATED`.
- Postcondición: `StrategyCreated`.
- Alternos: si faltan datos obligatorios, se rechaza antes de llegar al dominio (validación de la capa `interfaces`).

## UC-02 Actualizar Strategy

- Módulo: `strategy`.
- Actor: Trader.
- Precondición: la Strategy existe.
- Flujo principal: el Trader modifica parámetros (reglas, indicadores) de una Strategy existente.
- Postcondición: sin evento propio en el catálogo actual de DOM-002 — pendiente decidir si se agrega `StrategyUpdated` en DOM-007 v1.1.
- Alternos: si la Strategy está `ACTIVE`, evaluar si se permite editar en caliente o se exige desactivarla primero (decisión abierta, ver sección 3).

## UC-03 Activar Strategy

- Módulo: `strategy`.
- Actor: Trader.
- Precondición: la Strategy tiene al menos una regla de entrada y una de salida (invariante DOM-004).
- Flujo principal: Trader activa la Strategy → pasa a `ACTIVE`.
- Postcondición: `StrategyActivated`.
- Alternos: si no cumple el invariante, se rechaza con el motivo.

## UC-04 Desactivar Strategy

- Módulo: `strategy`.
- Actor: Trader.
- Precondición: la Strategy está `ACTIVE`.
- Flujo principal: Trader desactiva la Strategy → pasa a `INACTIVE`. Deja de evaluarse contra el mercado.
- Postcondición: `StrategyDeactivated`.

## UC-05 Cancelar Order

- Módulo: `order` (Order Execution).
- Actor: Trader.
- Precondición: la Order existe y está en `CREATED` o `SUBMITTED` (no es un estado terminal — DOM-006).
- Flujo principal: Trader cancela la orden antes de que se ejecute.
- Postcondición: `OrderRejected` (razón: cancelada por el Trader) — o se agrega un evento propio `OrderCancelled` en una futura versión de DOM-007 si se necesita distinguir de un rechazo del exchange.
- Alternos: si la Order ya está `EXECUTED` o `REJECTED`, se rechaza el comando (invariante de estado terminal).

## UC-06 Cerrar Position manualmente

- Módulo: `portfolio` (Portfolio Management).
- Actor: Trader.
- Precondición: la Position existe y está `OPEN`.
- Flujo principal: Trader decide cerrar la posición antes de que la Strategy lo haga automáticamente (ver PR-07).
- Postcondición: `PositionClosed`.
- Alternos: si ya está `CLOSED`, se rechaza (invariante DOM-006).

## UC-07 Actualizar RiskProfile

- Módulo: `portfolio`.
- Actor: Trader.
- Precondición: el Portfolio existe.
- Flujo principal: Trader cambia `maxRiskPerTrade`, `stopLoss`, `takeProfit` o `maxExposure`. Se reemplaza el Value Object completo (DOM-004/DOM-005).
- Postcondición: `RiskProfileUpdated` (con valor anterior y nuevo).
- Alternos: si `maxRiskPerTrade > maxExposure`, se rechaza (invariante DOM-006, sección 2.4).

---

# 2. Procesos reactivos (disparados por eventos)

## PR-01 Analizar mercado y generar señal

- Módulo: `signal` (Signal Generation).
- Disparador: `MarketDataReceived` (Integration Event, DOM-007 4.7), para cada Strategy `ACTIVE` sobre ese `tradingPair`.
- Flujo principal: evalúa las reglas de la Strategy contra la nueva vela/precio.
- Postcondición: `SignalGenerated` (si se detecta una oportunidad) o ningún evento (si no hay señal).

## PR-02 Validar riesgo de una señal

- Módulo: `risk` (Risk Management).
- Disparador: `SignalGenerated`.
- Flujo principal: el Risk Manager consulta el `RiskProfile` del Portfolio y las Position abiertas (consistencia eventual, DOM-006 sección 4) para decidir si autoriza avanzar hacia una Order.
- Postcondición: `RiskApproved` o `RiskRejected` (con motivo).

## PR-03 Crear Order desde señal aprobada

- Módulo: `order`.
- Disparador: `RiskApproved`.
- Flujo principal: se crea la Order en `CREATED`, referenciando `signalId` y `strategyId`.
- Postcondición: `OrderCreated`.

## PR-04 Enviar Order al exchange

- Módulo: `order`.
- Disparador: `OrderCreated`.
- Flujo principal: se envía la orden al [[Exchange]] configurado vía el Port/Adapter correspondiente (ADR-004).
- Postcondición: `OrderSubmitted`. La confirmación de ejecución llega después, de forma asíncrona, desde el Exchange.
- Alternos: si el Exchange rechaza la orden al enviarla, `OrderRejected`.

## PR-05 Abrir Position desde Order ejecutada

- Módulo: `portfolio`.
- Disparador: `OrderExecuted`.
- Flujo principal: se crea una nueva Position (o se ajusta una existente, si es una ejecución parcial sobre el mismo `tradingPair` y dirección) con `entryPrice` y `quantity` de la ejecución.
- Postcondición: `PositionOpened`.

## PR-06 Actualizar Position con datos de mercado

- Módulo: `portfolio`.
- Disparador: `MarketDataReceived`, para cada Position `OPEN` del mismo `tradingPair`.
- Flujo principal: recalcula `currentPrice` y `pnl`.
- Postcondición: `PositionUpdated`.
- Invariante: no se ejecuta si la Position ya está `CLOSED` (DOM-006).

## PR-07 Cerrar Position automáticamente (stop loss / take profit)

- Módulo: `portfolio`, en coordinación con `risk`.
- Disparador: `PositionUpdated`.
- Flujo principal: si el `pnl` actual cruza el `stopLoss` o `takeProfit` del `RiskProfile`, se dispara el cierre automático.
- Postcondición: `PositionClosed`.

---

# 3. Decisiones abiertas

- UC-02 (Actualizar Strategy): falta decidir si se permite editar una Strategy `ACTIVE` en caliente, o si se exige desactivarla primero. Impacta si `StrategyUpdated` necesita agregarse a DOM-007.
- UC-05 (Cancelar Order): falta decidir si "cancelada por el Trader" reutiliza `OrderRejected` o merece su propio evento `OrderCancelled`, para poder distinguir en reportes una orden rechazada por el exchange de una cancelada a propósito.

---

# Estado

Primera versión de Casos de Uso, cubre el flujo principal de DOM-002. No incluye todavía casos de uso de consulta (queries: listar estrategias, ver historial de una posición, etc.), que se agregarán cuando se diseñe la capa de `interfaces`/API.

Pendiente:

- Resolver las decisiones abiertas de la sección 3.
- Siguiente paso: empezar a implementar en código el primer módulo (`strategy`, siguiendo ADR-004), comenzando por UC-01.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 17/07/2026 | Miler Ayarza | Creación del documento |
