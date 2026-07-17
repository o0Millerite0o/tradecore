**Versión:** 1.2 
**Estado:** En elaboración 
**Fecha de creación:** 15/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Identificar, mediante Event Storming, el flujo principal del negocio de TradeCore: actores, comandos, eventos de dominio, agregados y una primera aproximación a los Bounded Context.

Este documento representa la primera versión del modelo de dominio y es la base para DOM-003 (Bounded Contexts), DOM-004 (Entities), DOM-005 (Value Objects), DOM-006 (Aggregates) y DOM-007 (Domain Events).

## Alcance

- Actores del dominio (externos e internos).
- Flujo principal del negocio (happy path).
- Comandos del dominio.
- Eventos de dominio.
- Agregados principales (primera aproximación).
- Bounded Context inicial.

---

# 1. Actores del dominio

## 1.1 Actores externos

### Trader

Persona que define estrategias, configura parámetros de riesgo y supervisa operaciones.

Responsabilidades:

- Crear [[Strategy]].
- Activar o desactivar [[Strategy]].
- Revisar [[Signal]].
- Consultar [[Position]].
- Analizar resultados de las operaciones.

---

### [[Exchange]]

Proveedor externo donde se ejecutan las operaciones.

Ejemplos: Binance, Bybit, LBank.

Responsabilidades:

- Recibir [[Order]].
- Ejecutar operaciones.
- Entregar información de [[Market]].
- Reportar estados de ejecución.

---

### [[Market]] Data Provider

Fuente externa de información financiera.

Responsabilidades:

- Proveer precios.
- Entregar velas OHLCV.
- Proporcionar indicadores.

---

## 1.2 Componentes internos del dominio

A diferencia de los actores externos, estos no son "actores" en el sentido estricto de Event Storming, sino políticas/servicios de dominio que reaccionan a eventos y ejecutan comandos. Se documentan aquí porque participan directamente en el flujo principal.

### Trading Engine

Motor encargado de ejecutar las reglas de trading.

Responsabilidades:

- Evaluar [[Strategy]].
- Analizar datos de [[Market]].
- Generar [[Signal]].
- Crear [[Order]].
- Gestionar [[Position]].

---

### Risk Manager

Componente encargado del control de riesgo.

Responsabilidades:

- Validar tamaño de [[Position]].
- Aplicar límites.
- Calcular exposición.
- Autorizar operaciones.

---

# 2. Flujo principal del dominio (happy path)

```text
Trader
  |
  v
Create Strategy
  |
  v
Strategy Created
  |
  v
Market Data Received
  |
  v
Analyze Market
  |
  v
Signal Generated
  |
  v
Risk Validation
  |
  v
Order Created
  |
  v
Order Submitted
  |
  v
Order Executed
  |
  v
Position Opened
  |
  v
Position Managed
  |
  v
Position Closed
```

Este es el camino feliz. Los caminos alternativos (`RiskRejected`, `OrderRejected`) están listados en la sección de eventos y deberán modelarse como flujos independientes cuando diseñemos los Casos de Uso.

---

# 3. Comandos del dominio

Los comandos representan una intención de cambio en el sistema. Pueden ser aceptados (y generar un evento) o rechazados.

## 3.1 [[Strategy]]

| Comando | Descripción |
|---|---|
| CreateStrategy | Crear una estrategia de trading. |
| UpdateStrategy | Modificar parámetros de una estrategia. |
| ActivateStrategy | Activar una estrategia. |
| DeactivateStrategy | Desactivar una estrategia. |

## 3.2 Trading

| Comando | Descripción |
|---|---|
| AnalyzeMarket | Evaluar condiciones del mercado. |
| GenerateSignal | Crear una señal de trading. |
| ValidateRisk | Evaluar reglas de riesgo. |
| CreateOrder | Crear una orden. |
| SubmitOrder | Enviar la orden al exchange. |
| CancelOrder | Cancelar una orden. |

## 3.3 [[Portfolio]]

| Comando | Descripción |
|---|---|
| OpenPosition | Abrir una posición. |
| UpdatePosition | Actualizar una posición existente. |
| ClosePosition | Cerrar una posición. |

---

# 4. Eventos de dominio

Los eventos representan hechos ya ocurridos e inmutables en el sistema. Se nombran siempre en pasado.

## 4.1 [[Strategy]]

| Evento | Descripción |
|---|---|
| StrategyCreated | La estrategia fue creada. |
| StrategyActivated | La estrategia fue activada. |
| StrategyDeactivated | La estrategia fue desactivada. |

## 4.2 Trading

| Evento | Descripción |
|---|---|
| MarketDataReceived | Se recibieron datos del mercado. |
| SignalGenerated | Se generó una señal de trading. |
| RiskApproved | El riesgo fue evaluado y aprobado. |
| RiskRejected | El riesgo fue evaluado y rechazado. |
| OrderCreated | La orden fue creada. |
| OrderSubmitted | La orden fue enviada al exchange. |
| OrderExecuted | La orden fue ejecutada. |
| OrderRejected | La orden fue rechazada. |

## 4.3 [[Portfolio]]

| Evento | Descripción |
|---|---|
| PositionOpened | Se abrió una nueva posición. |
| PositionUpdated | La posición fue modificada. |
| PositionClosed | La posición fue cerrada. |
| RiskProfileUpdated | El Trader actualizó su perfil de riesgo (se registra el valor anterior y el nuevo). |

---

# 5. Agregados principales (primera aproximación)

Esta es una primera lectura de los agregados detectados durante el Event Storming. La definición formal (raíz de agregado, invariantes, entidades internas) se hará en DOM-006 - Aggregates.

## [[Strategy]]

Representa una lógica de trading.

Contiene: nombre, mercado objetivo, indicadores, reglas de entrada, reglas de salida, estado.

## [[Order]]

Representa una instrucción enviada al exchange.

Contiene: tipo de orden, símbolo, cantidad, precio, estado.

## [[Position]]

Representa una exposición activa del portfolio.

Contiene: símbolo, dirección, tamaño, precio de entrada, precio actual, PnL.

## [[Portfolio]]

Representa la gestión del capital.

Contiene: balance, capital disponible, posiciones activas, historial.

## RiskProfile

Representa las reglas de protección del capital.

Contiene: riesgo máximo por operación, stop loss, take profit, exposición máxima.

---

# 6. Bounded Context inicial

Esta es la primera aproximación a los límites del dominio detectada en este ejercicio. El detalle completo (responsabilidades, context map, relaciones entre contextos) ya está desarrollado en DOM-003 - Bounded Contexts.

- **[[Strategy]] Management** — crear y administrar estrategias.
- **[[Market]] Data** — obtener y procesar información del mercado.
- **[[Signal]] Generation** — detectar oportunidades de entrada o salida.
- **[[Order]] Execution** — comunicarse con exchanges.
- **[[Portfolio]] Management** — seguimiento de capital y posiciones.
- **Risk Management** — validar operaciones.

---

# Estado

Versión inicial del modelo de dominio (Event Storming).

Pendiente:

- DOM-004 - Entities: definir entidades y su ciclo de vida.
- DOM-005 - Value Objects: definir objetos de valor (por ejemplo Price, Symbol, PnL).
- DOM-006 - Aggregates: formalizar raíces de agregado e invariantes.
- DOM-007 - Domain Events: formalizar el contrato de cada evento (payload, versión, forma de publicación).

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 15/07/2026 | Miler Ayarza | Creación del documento |
| 1.1     | 17/07/2026 | Miler Ayarza | Se separaron actores externos de componentes internos, se agregó Alcance, se referenció DOM-003/004/005/006/007 y se aclaró que el flujo es el happy path |
| 1.2     | 17/07/2026 | Miler Ayarza | Se agregó el evento RiskProfileUpdated tras decidir en DOM-004 que RiskProfile es Value Object embebido (no Entity) |
