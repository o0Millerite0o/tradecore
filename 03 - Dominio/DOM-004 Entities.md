**Versión:** 1.1 
**Estado:** En elaboración 
**Fecha de creación:** 17/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Identificar y definir las Entidades del dominio de TradeCore: qué objetos tienen identidad propia y ciclo de vida, distinguiéndolos de los Value Objects (DOM-005) y estableciendo la base para DOM-006 - Aggregates.

## Contexto

En DOM-002 - Event Storming identificamos cinco agregados candidatos: [[Strategy]], [[Order]], [[Position]], [[Portfolio]] y RiskProfile. Antes de formalizarlos como agregados, hay que clasificar cada uno (y sus partes) como Entity o Value Object.

---

# 1. Entity vs Value Object

| | Entity | Value Object |
|---|---|---|
| Identidad | Tiene un id propio que se mantiene estable durante todo su ciclo de vida | No tiene identidad; se define solo por sus atributos |
| Igualdad | Dos instancias son iguales si su id es igual, aunque cambien sus atributos | Dos instancias son iguales si todos sus atributos son iguales |
| Mutabilidad | Puede cambiar de estado a lo largo del tiempo | Inmutable: cualquier cambio crea una nueva instancia |
| Ejemplo en TradeCore | Una [[Order]] sigue siendo la misma orden aunque cambie de `CREATED` a `EXECUTED` | Un `Price` de 60000 USDT es igual a cualquier otro `Price` de 60000 USDT |

---

# 2. Clasificación de los candidatos de DOM-002

## [[Strategy]] — Entity

- Identidad: `strategyId`.
- Atributos mutables: `status`, reglas de entrada/salida, indicadores.
- Ciclo de vida: `Created → Activated ⇄ Deactivated`.
- Invariante propuesta: no puede activarse sin al menos una regla de entrada y una de salida definidas.

## [[Order]] — Entity

- Identidad: `orderId`.
- Atributos mutables: `status`.
- Ciclo de vida: `Created → Submitted → Executed | Rejected`.
- Invariante propuesta: una vez en estado `Executed` o `Rejected`, no puede volver a cambiar de estado.

## [[Position]] — Entity

- Identidad: `positionId`.
- Atributos mutables: `currentPrice`, `pnl`, `status`.
- Ciclo de vida: `Opened → Managed → Closed`.
- Invariante propuesta: no puede cerrarse dos veces; no tiene `currentPrice` antes de abrirse.

## [[Portfolio]] — Entity

- Identidad: `portfolioId`.
- Atributos mutables: `balance`, posiciones activas.
- Ciclo de vida: persiste durante toda la vida de la cuenta del Trader.
- Invariante propuesta: `balance` nunca puede quedar negativo.

## RiskProfile — Value Object (decidido)

Dos configuraciones de riesgo con los mismos valores (`maxRiskPerTrade`, `stopLoss`, `takeProfit`, `maxExposure`) son, para el negocio, la misma configuración: no existe un concepto de "esta instancia de RiskProfile en particular" independiente de sus atributos. Por eso no necesita `riskProfileId` propio.

- Vive embebido dentro de [[Portfolio]], que es quien lo posee y lo reemplaza como bloque inmutable cuando el Trader lo actualiza.
- La trazabilidad de cambios (qué necesitábamos al considerarlo Entity) se resuelve con un evento de dominio en vez de con identidad propia: [[Portfolio]] emite `RiskProfileUpdated` con el valor anterior y el nuevo cada vez que cambia. Ver DOM-002, sección 4.3.
- Se formalizará como Value Object en DOM-005.

---

# 3. Relaciones preliminares entre entidades

```text
Portfolio 1 ---- N Position
Portfolio 1 ---- 1 RiskProfile   (embebido, no es una fila/tabla propia)
Strategy   1 ---- N Order
Order      N ---- 1 Position   (varias órdenes pueden afectar la misma posición: entradas parciales, ajustes, cierre)
```

Estas cardinalidades son una primera hipótesis; se validarán al formalizar los agregados en DOM-006.

---

# 4. Insumo para DOM-005 - Value Objects

Atributos detectados que no tienen identidad propia y son buenos candidatos a Value Object:

- `Price`, `Quantity`, `PnL` — valores numéricos con reglas propias (por ejemplo, no negativos).
- `Symbol` (ej. BTCUSDT) — par de negociación.
- `OrderType` (`MARKET`, `LIMIT`, `STOP_LOSS`).
- `PositionDirection` (`LONG`, `SHORT`).
- `StrategyStatus`, `OrderStatus`, `PositionStatus` — como enums o Value Objects según se decida en DOM-005.
- `RiskProfile` (`maxRiskPerTrade`, `stopLoss`, `takeProfit`, `maxExposure`) — Value Object completo, embebido en [[Portfolio]].

---

# Estado

Versión inicial de clasificación de Entidades.

Pendiente:

- Validar cardinalidades de la sección 3 contra los casos de uso reales.
- DOM-005 - Value Objects: formalizar los atributos listados en la sección 4, incluido RiskProfile.
- DOM-006 - Aggregates: decidir qué Entities son raíz de agregado y cuáles quedan dentro de otro agregado.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 17/07/2026 | Miler Ayarza | Creación del documento |
| 1.1     | 17/07/2026 | Miler Ayarza | RiskProfile reclasificado de Entity a Value Object embebido en Portfolio; trazabilidad resuelta con el evento RiskProfileUpdated |
