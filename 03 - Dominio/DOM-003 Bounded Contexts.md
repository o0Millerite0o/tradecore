**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 15/07/2026 
**Última actualización:** 15/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Definir los límites del dominio de TradeCore mediante Bounded Contexts siguiendo principios de Domain Driven Design.

Cada contexto representa una capacidad de negocio independiente con sus propias reglas, modelos y responsabilidades.

---

# 1. Vista general del dominio

```text
                         TradeCore

                              |
        ------------------------------------------------
        |              |              |                |
        v              v              v                v

 Strategy        Market Data     Execution       Portfolio
 Management                      Engine          Management

        |              |              |                |

        ------------------------------------------------
                              |
                              v

                       Risk Management
```

# 2. Strategy Management Context

## Responsabilidad

Gestionar la definición y configuración de estrategias de trading.

## Objetos principales

### [[Strategy]]

Representa una estrategia de inversión.

Atributos:

- id
- name
- description
- market
- timeframe
- status

---

### StrategyRule

Representa una condición dentro de una [[Strategy]].

Ejemplos:

- RSI menor a 30.
- Cruce de medias móviles.
- Ruptura de resistencia.

---

## Eventos

- StrategyCreated
- StrategyActivated
- StrategyDeactivated

---

# 3. [[Market]] Data Context

## Responsabilidad

Obtener y procesar información del mercado.

## Fuentes

- [[Exchange]] APIs.
- [[Market]] providers.
- WebSockets.

---

## Objetos principales

### MarketPrice

Precio actual de un activo.

---

### Candle

Representa una vela OHLCV.

Datos:

- open
- high
- low
- close
- volume
- timestamp

---

## Eventos

- MarketDataReceived
- CandleClosed
- PriceUpdated

---

# 4. Signal Generation Context

## Responsabilidad

Analizar condiciones del mercado y generar señales.

---

## Objetos principales

### [[Signal]]

Representa una oportunidad detectada.

Ejemplo:

BTCUSDT

Acción:

BUY

Confianza:

85%

---

## Eventos

- SignalGenerated
- SignalRejected

---

# 5. Risk Management Context

## Responsabilidad

Controlar la exposición antes de ejecutar operaciones.

---

## Objetos principales

### RiskProfile

Configuración de riesgo.

Ejemplo:

```
Maximum Risk: 2%
Stop Loss: 5%
Maximum Positions: 3
```

---

### RiskEvaluation

Resultado del análisis.

Estados:

- APPROVED
- REJECTED

---

## Eventos

- RiskApproved
- RiskRejected

---

# 6. [[Order]] Execution Context

## Responsabilidad

Comunicación con exchanges y ejecución de órdenes.

---

## Objetos principales

### [[Order]]

Representa una solicitud de operación.

Tipos:

- [[MARKET]]
- LIMIT
- STOP_LOSS

---

### ExchangeAdapter

Adaptador hacia proveedores externos.

Ejemplos:

- BinanceAdapter
- BybitAdapter
- LBankAdapter

---

## Eventos

- OrderCreated
- OrderSubmitted
- OrderExecuted
- OrderRejected

---

# 7. [[Portfolio]] Management Context

## Responsabilidad

Administrar capital y [[Position]].

---

## Objetos principales

### [[Portfolio]]

Representa el capital administrado.

---

### [[Position]]

Representa una operación activa.

Ejemplo:

```
BTCUSDT
LONG
Entry: 60000
Current: 62000
PnL: +3.3%
```

---

## Eventos

- PositionOpened
- PositionUpdated
- PositionClosed

# 8. Relaciones entre Contextos

```
Strategy Management

        |
        v

Signal Generation

        |
        v

Risk Management

        |
        v

Order Execution

        |
        v

Portfolio Management
```

Market Data alimenta:

```
Market Data
      |
      +----> Signal Generation
      |
      +----> Risk Management
      |
      +----> Portfolio Management
```

---

# 9. Context Map inicial

|Contexto|Tipo|Comunicación|
|---|---|---|
|Strategy Management|Core Domain|REST / Events|
|Market Data|Supporting|Events|
|Signal Generation|Core Domain|Events|
|Risk Management|Core Domain|Events|
|Order Execution|Core Domain|REST / Events|
|Portfolio Management|Core Domain|Events|

---

# 10. Decisiones iniciales

## Separación de modelos

Cada contexto tendrá sus propios modelos.

Ejemplo:

[[Market]] Data:

```
MarketPrice
```

[[Portfolio]]:

```
PositionPrice
```

No compartirán la misma clase.

---

## Comunicación

Primera versión:

REST para operaciones síncronas.

Eventos para procesos internos.

Futura implementación:

Kafka / RabbitMQ.

---

# Estado

Versión inicial de límites del dominio.

Pendiente:

- Diseñar arquitectura técnica.
- Definir módulos Java.
- Definir entidades JPA.
- Diseñar APIs.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 15/07/2026 | Miler Ayarza | Creación del documento |
