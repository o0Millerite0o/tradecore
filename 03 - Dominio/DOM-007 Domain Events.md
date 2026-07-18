**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 17/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Formalizar el contrato técnico de cada evento identificado en DOM-002: payload exacto, versión y mecanismo de publicación, para poder implementarlos de forma consistente en código.

## Contexto

DOM-002 identificó los eventos a alto nivel (solo nombre y descripción). Este documento define qué datos lleva cada uno, cómo se publican en esta primera etapa (Modular Monolith, ADR-003 / Arquitectura Hexagonal, ADR-004) y qué reglas de nomenclatura y versión seguirán todos.

---

# 1. Domain Event vs Integration Event

No todos los "eventos" del flujo de DOM-002 nacen de uno de nuestros Aggregate Roots. Conviene distinguir dos tipos:

| Tipo | Definición | Ejemplo en TradeCore |
|---|---|---|
| Domain Event | Lo emite uno de nuestros Aggregate Roots (DOM-006) después de un cambio exitoso. | `OrderCreated`, `PositionOpened` |
| Integration Event | Dato que entra desde un sistema externo, cruzando el límite del Bounded Context. No lo emite un Aggregate nuestro. | `MarketDataReceived`, proveniente del Market Data Provider |

Esta distinción importa porque un Integration Event normalmente pasa primero por una capa Anti-Corrupción (adapter) que lo traduce a nuestro lenguaje ubicuo antes de que el dominio lo procese.

---

# 2. Estructura común (envelope)

Todo evento — Domain o Integration — comparte estos campos:

| Campo | Tipo | Descripción |
|---|---|---|
| `eventId` | UUID | Identificador único del evento (para trazabilidad e idempotencia). |
| `eventType` | String | Nombre del evento, ej. `"OrderCreated"`. |
| `eventVersion` | int | Versión del contrato del payload. Empieza en `1`. |
| `occurredOn` | Instant | Momento en que ocurrió el hecho (no cuándo se publicó). |

El resto de los campos es específico de cada evento (sección 4).

---

# 3. Mecanismo de publicación (MVP)

Con arquitectura Modular Monolith (ADR-003), no hace falta un broker externo todavía. Para esta primera etapa:

- Los eventos se publican en proceso, usando `ApplicationEventPublisher` de Spring, después de que la transacción del Aggregate Root que los originó se confirme (`@TransactionalEventListener` en el lado consumidor).
- Todos los eventos implementan una interfaz o record base `DomainEvent` con `eventId`, `eventType`, `eventVersion`, `occurredOn`.
- Gracias a la Arquitectura Hexagonal (ADR-004), el dominio solo conoce que "publica un evento" — no sabe si el transporte es en memoria, Kafka o RabbitMQ. Cuando el proyecto lo requiera (PV-002, sección 9: Kafka/RabbitMQ futuro), se cambia el adapter de salida sin tocar el dominio.
- Entrega garantizada (Transactional Outbox) queda fuera de alcance del MVP; se revisará si aparecen problemas reales de eventos perdidos.

---

# 4. Catálogo de eventos

## 4.1 [[Strategy]]

### StrategyCreated

`strategyId`, `name`, `tradingPair`, `timeframe`, `occurredOn`.

### StrategyActivated

`strategyId`, `occurredOn`.

### StrategyDeactivated

`strategyId`, `occurredOn`.

---

## 4.2 Signal

Decisión pendiente en DOM-006: si Signal es su propio Aggregate o un concepto interno de Order. Se resuelve aquí: **Signal no es un Aggregate.** Es un concepto efímero — el resultado de evaluar una Strategy contra el mercado — que no necesita persistirse con ciclo de vida propio. Se le asigna un `signalId` únicamente para poder correlacionar los eventos que le siguen (`RiskApproved`/`RiskRejected`, `OrderCreated`).

### SignalGenerated

`signalId`, `strategyId`, `tradingPair`, `action` (`BUY` | `SELL`), `confidence` (Percentage), `occurredOn`.

---

## 4.3 Risk

La validación de riesgo ocurre sobre la Signal, antes de que exista la Order (ver flujo de DOM-002).

### RiskApproved

`signalId`, `portfolioId`, `occurredOn`.

### RiskRejected

`signalId`, `portfolioId`, `reason`, `occurredOn`.

---

## 4.4 [[Order]]

### OrderCreated

`orderId`, `signalId`, `strategyId`, `tradingPair`, `type` (OrderType), `quantity`, `price`, `occurredOn`.

### OrderSubmitted

`orderId`, `occurredOn`.

### OrderExecuted

`orderId`, `executedPrice`, `executedQuantity`, `occurredOn`.

### OrderRejected

`orderId`, `reason`, `occurredOn`.

---

## 4.5 [[Position]]

### PositionOpened

`positionId`, `portfolioId`, `tradingPair`, `direction` (PositionDirection), `quantity`, `entryPrice`, `occurredOn`.

### PositionUpdated

`positionId`, `currentPrice`, `pnl`, `occurredOn`.

### PositionClosed

`positionId`, `exitPrice`, `realizedPnl`, `occurredOn`.

---

## 4.6 [[Portfolio]]

### RiskProfileUpdated

`portfolioId`, `previousRiskProfile` (snapshot), `newRiskProfile` (snapshot), `occurredOn`.

---

## 4.7 Integration Event: Market Data

### MarketDataReceived

`tradingPair`, `timeframe`, `candle` (`open`, `high`, `low`, `close`, `volume`), `occurredOn`.

Proviene del Market Data Provider (actor externo, DOM-002 sección 1.1), no de un Aggregate Root propio.

---

# 5. Reglas de nomenclatura y versión

- Nombre siempre en pasado, sin abreviaturas (`OrderExecuted`, no `OrdExec`).
- Un evento nace en `eventVersion = 1`. Si el payload necesita un cambio incompatible, se crea una nueva versión en paralelo (`OrderCreatedV2`) en vez de romper el contrato existente — los consumidores viejos siguen funcionando mientras migran.
- `occurredOn` es siempre el momento del hecho de negocio, no el momento de publicación ni de procesamiento.

---

# Estado

Con este documento se cierra la fase de **Modelo de Dominio** (Lenguaje Ubicuo → Event Storming → Entities → Value Objects → Aggregates → Domain Events).

Pendiente:

- Definir mecanismo de idempotencia en los consumidores (evitar procesar el mismo `eventId` dos veces).
- Evaluar Transactional Outbox si se necesita garantía de entrega más fuerte antes de migrar a Kafka/RabbitMQ.
- Siguiente paso: **04 - Casos de Uso**.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 17/07/2026 | Miler Ayarza | Creación del documento |
