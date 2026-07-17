**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 17/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo

Formalizar los Value Objects del dominio de TradeCore identificados en DOM-004 - Entities: atributos sin identidad propia, inmutables, con igualdad por valor y responsables de autovalidarse.

## Contexto

DOM-004 dejó una lista de candidatos (sección 4) que aquí se formalizan con sus atributos, invariantes y comportamiento. También se decide, para cada uno, si conviene modelarlo como un Value Object propio o si basta con un enum de Java.

---

# 1. Value Object vs Enum

No todo concepto "sin identidad" necesita una clase Value Object completa. Si es solo un conjunto fijo y discreto de valores, sin reglas de validación ni comportamiento propio, un enum de Java es más simple y suficiente. Se reserva Value Object para conceptos con invariantes o comportamiento (aritmética, comparación, formato, normalización).

| Concepto | Tipo recomendado | Motivo |
|---|---|---|
| Price | Value Object | Invariante (> 0) y comportamiento (sumar, comparar). |
| Quantity | Value Object | Igual que Price. |
| PnL | Value Object | Se calcula y se compara; combina monto absoluto y porcentaje. |
| Percentage | Value Object | Reutilizado por RiskProfile y PnL; invariante de rango. |
| TradingPair | Value Object | Valida formato y normaliza; ya está en el lenguaje ubicuo (DOM-001). |
| RiskProfile | Value Object compuesto | Decidido en DOM-004. |
| OrderType | Enum | Conjunto fijo, sin reglas adicionales. |
| PositionDirection | Enum | Igual. |
| StrategyStatus / OrderStatus / PositionStatus | Enum | Estados discretos con transiciones fijas (ver DOM-002 / DOM-004). |

---

# 2. Value Objects formales

## Price

- Atributos: `amount` (BigDecimal).
- Invariantes: `amount > 0`; escala decimal fija (a definir junto con el tipo de columna en la base de datos).
- Comportamiento: `add`, `subtract`, `isGreaterThan`, `isLessThan`.

## Quantity

- Atributos: `amount` (BigDecimal).
- Invariantes: `amount > 0`.
- Comportamiento: `add`, `subtract` (para ejecuciones parciales), `isZero`.

## PnL

- Atributos: `amount` (BigDecimal, absoluto), `percentage` (Percentage, relativo).
- Invariantes: sin restricción de signo (puede ser negativo).
- Comportamiento: se calcula a partir de `entryPrice`, `currentPrice`, `quantity` y `direction` de [[Position]] (ver DOM-004).

## Percentage

- Atributos: `value` (BigDecimal).
- Invariantes: `0 <= value <= 100`. Se decide representar en base 100 (no como fracción 0–1) para que sea más legible al leer o loguear.
- Comportamiento: `of(double)`, `isGreaterThan`, `applyTo(Price)`.

## TradingPair

Nota de lenguaje: DOM-001 define este concepto como **Trading Pair**, pero en DOM-002/DOM-003/DOM-004 se usó informalmente la palabra "símbolo". A partir de aquí el nombre oficial en código es `TradingPair`; "symbol" queda solo como el string plano que lo compone (ej. `"BTCUSDT"`), no como nombre de clase.

- Atributos: `baseAsset` (String), `quoteAsset` (String).
- Invariantes: ninguno vacío; formato validado (mayúsculas, alfanumérico).
- Comportamiento: `toString()` → `"BTCUSDT"` o `"BTC/USDT"` según el formato que exija cada exchange.

## RiskProfile

Ya decidido como Value Object en DOM-004; aquí se formaliza.

- Atributos: `maxRiskPerTrade` (Percentage), `stopLoss` (Percentage), `takeProfit` (Percentage), `maxExposure` (Percentage).
- Invariantes: `maxRiskPerTrade <= maxExposure`; cada porcentaje ya viene validado por `Percentage`.
- Comportamiento: métodos `withStopLoss(...)`, `withMaxExposure(...)`, etc. devuelven una nueva instancia (inmutabilidad). Cuando [[Portfolio]] reemplaza su `RiskProfile`, emite `RiskProfileUpdated` (DOM-002, sección 4.3).

---

# 3. Enums del dominio

## OrderType

`MARKET`, `LIMIT`, `STOP_LOSS`.

## PositionDirection

`LONG`, `SHORT`.

## StrategyStatus

`CREATED`, `ACTIVE`, `INACTIVE` — alineado con el ciclo `Created → Activated ⇄ Deactivated` de DOM-004.

## OrderStatus

`CREATED`, `SUBMITTED`, `EXECUTED`, `REJECTED` — `EXECUTED` y `REJECTED` son estados terminales (DOM-004).

## PositionStatus

`OPEN`, `CLOSED`.

Nota: en el flujo de DOM-002 aparece "Position Managed" entre Opened y Closed, pero no es un estado adicional — es la actividad continua de actualizar `currentPrice` y `PnL` mientras la posición sigue `OPEN`. Por eso `PositionStatus` solo necesita dos valores.

---

# 4. Pendiente para DOM-006 - Aggregates

- Confirmar que `Price` se usa consistentemente en [[Order]] (precio de la orden) y en [[Position]] (`entryPrice`, `currentPrice`).
- Definir la escala decimal fija (por ejemplo 8 decimales para cripto) junto con el diseño de base de datos.
- Formalizar la raíz de agregado por cada Bounded Context (DOM-003).

# Estado

Versión inicial de Value Objects y Enums del dominio.

Pendiente:

- Validar `TradingPair` contra el formato real que exponen las APIs de LBank/Binance/Bybit.
- DOM-006 - Aggregates: usar estas definiciones para formalizar raíces de agregado e invariantes a nivel de agregado.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 17/07/2026 | Miler Ayarza | Creación del documento |
