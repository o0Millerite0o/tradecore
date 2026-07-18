**Versión:** 1.1 
**Estado:** En elaboración 
**Fecha de creación:** 17/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Formalizar los Aggregates del dominio de TradeCore a partir de las Entities (DOM-004) y Value Objects (DOM-005): definir raíz de agregado, qué queda encapsulado dentro de cada uno, sus invariantes, y cómo se referencian entre sí.

## Contexto

Un Aggregate es un clúster de Entities y Value Objects tratado como una sola unidad de consistencia transaccional, con una Entity actuando como Aggregate Root (AR): el único objeto que el resto del sistema puede referenciar directamente. Todo cambio a los miembros internos del agregado pasa por el AR, que es responsable de mantener sus invariantes.

Regla general que se sigue en este documento: agregados pequeños, y referencias entre agregados distintos siempre por id, nunca por objeto.

---

# 1. Decisión de diseño: Position como agregado independiente

Portfolio **no** contiene sus Position como parte del mismo agregado.

Motivo: en un sistema de trading, `currentPrice` y `PnL` de cada posición se actualizan constantemente (cada tick de mercado). Si Portfolio incluyera todas sus Position como un solo agregado, cada actualización de una posición obligaría a cargar y guardar el Portfolio completo, bloqueando la escritura concurrente de las demás posiciones del mismo trader.

En su lugar:

- [[Position]] es su propio Aggregate Root y referencia a su Portfolio solo mediante `portfolioId`.
- [[Portfolio]] no mantiene una colección de Position en memoria; consulta sus posiciones abiertas a través de un read model / repositorio cuando lo necesita.
- Las reglas que cruzan varias posiciones (por ejemplo, exposición total del Portfolio) no se validan como invariante transaccional de un único agregado. Se validan como una consulta del Risk Manager antes de crear la orden (`ValidateRisk`, ver DOM-002), aceptando consistencia eventual entre agregados.

---

# 2. Aggregates definidos

## 2.1 Strategy (Aggregate Root: [[Strategy]])

Contiene:

- `strategyId`, `name`, `description`, `market` (TradingPair), `timeframe`, `status` (StrategyStatus).
- Lista de `StrategyRule` (reglas de entrada/salida) — Value Objects internos, viven y mueren con la Strategy.

Invariantes:

- No puede pasar a `ACTIVE` sin al menos una regla de entrada y una de salida.
- Solo el propio agregado modifica sus reglas; no se exponen para edición directa desde fuera.

Referencias externas (solo por id): ninguna entra a Strategy; Order sale de Strategy por `strategyId`.

---

## 2.2 Order (Aggregate Root: [[Order]])

Contiene:

- `orderId`, `tradingPair` (TradingPair), `type` (OrderType), `quantity` (Quantity), `price` (Price), `status` (OrderStatus).
- Referencias por id: `strategyId` (de qué Strategy vino), `signalId` (opcional, si se modela Signal como su propio concepto).

Invariantes:

- Transiciones de estado válidas únicamente: `CREATED → SUBMITTED → EXECUTED | REJECTED`.
- `EXECUTED` y `REJECTED` son estados terminales: una vez ahí, el agregado rechaza cualquier transición adicional.
- `quantity` y `price` ya llegan validados por sus Value Objects (> 0).

---

## 2.3 Position (Aggregate Root: [[Position]])

Contiene:

- `positionId`, `tradingPair` (TradingPair), `direction` (PositionDirection), `quantity` (Quantity), `entryPrice` (Price), `currentPrice` (Price), `pnl` (PnL), `status` (PositionStatus).
- Referencias por id: `portfolioId`, y la lista de `orderId` que la originaron o la ajustaron (ejecuciones parciales).

Invariantes:

- No puede actualizar `currentPrice` ni `pnl` si `status = CLOSED`.
- No puede cerrarse dos veces (`ClosePosition` sobre una Position ya `CLOSED` se rechaza).
- `entryPrice` es inmutable una vez abierta la posición.

---

## 2.4 Portfolio (Aggregate Root: [[Portfolio]])

Contiene:

- `portfolioId`, `balance` (Price), `riskProfile` (RiskProfile — Value Object embebido, DOM-004/DOM-005).

No contiene:

- La colección de Position activas (ver decisión de la sección 1).

Invariantes:

- `balance` nunca queda negativo.
- `riskProfile` solo se reemplaza como bloque completo (inmutable); cada reemplazo emite `RiskProfileUpdated`.

Actualización de `balance`: se realiza reaccionando a eventos de [[Position]] (`PositionClosed` realiza la ganancia o pérdida sobre el balance). Para el MVP, sin apalancamiento ni reserva de margen (ver PV-002: no se opera con dinero real todavía), no se modela una reserva de margen al abrir posición — se revisará cuando se incorpore leverage.

---

# 3. Relaciones entre agregados (por id, no por objeto)

```text
Strategy  (AR) <---- strategyId ---- Order (AR)
Portfolio (AR) <---- portfolioId ---- Position (AR)
Position  (AR) <---- orderId(s) ----  Order (AR)   (una posición referencia las órdenes que la originaron/ajustaron)
```

Ningún agregado contiene una colección viva de otro agregado. Todo cruce se resuelve con el id y, cuando se necesita agregación de datos (por ejemplo "todas las posiciones abiertas de un Portfolio"), se hace vía consulta/read model, no vía navegación de objetos.

---

# 4. Consistencia entre agregados

Reglas de negocio que cruzan más de un agregado (como validar que una nueva Order no exceda la exposición máxima del RiskProfile considerando todas las Position abiertas) se resuelven como **consistencia eventual**, coordinadas por un servicio de dominio (Risk Manager) que consulta el estado actual antes de autorizar el comando, no como invariante transaccional de un solo agregado.

Esto es consistente con el flujo ya modelado en DOM-002: `ValidateRisk` ocurre como paso explícito antes de `CreateOrder`, precisamente porque esa validación no puede vivir dentro del agregado Order ni del agregado Portfolio por separado.

---

# Estado

Versión inicial de Aggregates del dominio.

Pendiente:

- Revisar reserva de margen / apalancamiento cuando el proyecto avance más allá de paper trading.

Resuelto en DOM-007: Signal no es un Aggregate ni un Value Object interno de Order — es un concepto efímero (resultado de evaluar una Strategy) identificado solo por un `signalId` para correlacionar eventos.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 17/07/2026 | Miler Ayarza | Creación del documento |
| 1.1     | 17/07/2026 | Miler Ayarza | Se resolvió en DOM-007 el pendiente sobre Signal: no es Aggregate ni VO de Order, es un concepto efímero identificado por signalId |
