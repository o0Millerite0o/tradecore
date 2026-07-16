
## Objetivo

Identificar el flujo principal del negocio de TradeCore mediante comandos, eventos de dominio, actores y agregados.

Este documento representa la primera versión del modelo de dominio.

---

# 1. Actores del dominio

## Trader

Persona o sistema que define estrategias, configura parámetros de riesgo y supervisa operaciones.

Responsabilidades:

- Crear [[Strategy]].
- Activar o desactivar [[Strategy]].
- Revisar [[Signal]].
- Consultar [[Position]].
- Analizar resultados.

---

## Trading Engine

Motor encargado de ejecutar las reglas de trading.

Responsabilidades:

- Evaluar [[Strategy]].
- Analizar datos de [[Market]].
- Generar [[Signal]].
- Crear [[Order]].
- Gestionar [[Position]].

---

## [[Exchange]]

Proveedor externo donde se ejecutan las operaciones.

Ejemplos:

- Binance.
- Bybit.
- LBank.

Responsabilidades:

- Recibir [[Order]].
- Ejecutar operaciones.
- Entregar información de [[Market]].
- Reportar estados de ejecución.

---

## [[Market]] Data Provider

Fuente externa de información financiera.

Responsabilidades:

- Proveer precios.
- Entregar velas OHLCV.
- Proporcionar indicadores.

---

## Risk Manager

Componente encargado del control de riesgo.

Responsabilidades:

- Validar tamaño de [[Position]].
- Aplicar límites.
- Calcular exposición.
- Autorizar operaciones.

---

# 2. Flujo principal del dominio

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

# 3. Comandos del dominio

Los comandos representan acciones que generan cambios en el sistema.

## [[Strategy]]

|Comando|Descripción|
|---|---|
|CreateStrategy|Crear una estrategia de trading|
|UpdateStrategy|Modificar parámetros|
|ActivateStrategy|Activar una estrategia|
|DeactivateStrategy|Desactivar una estrategia|

---

## Trading

|Comando|Descripción|
|---|---|
|AnalyzeMarket|Evaluar condiciones del mercado|
|GenerateSignal|Crear una señal de trading|
|ValidateRisk|Evaluar reglas de riesgo|
|CreateOrder|Crear una orden|
|SubmitOrder|Enviar orden al exchange|
|CancelOrder|Cancelar una orden|

---

## [[Portfolio]]

|Comando|Descripción|
|---|---|
|OpenPosition|Abrir posición|
|UpdatePosition|Actualizar posición|
|ClosePosition|Cerrar posición|

---

# 4. Eventos de dominio

Los eventos representan hechos ocurridos en el sistema.

## [[Strategy]] Events

|Evento|Descripción|
|---|---|
|StrategyCreated|Estrategia creada|
|StrategyActivated|Estrategia activada|
|StrategyDeactivated|Estrategia desactivada|

---

## Trading Events

|Evento|Descripción|
|---|---|
|MarketDataReceived|Datos recibidos|
|SignalGenerated|Señal creada|
|RiskApproved|Riesgo aprobado|
|RiskRejected|Riesgo rechazado|
|OrderCreated|Orden creada|
|OrderSubmitted|Orden enviada|
|OrderExecuted|Orden ejecutada|
|OrderRejected|Orden rechazada|

---

## [[Portfolio]] Events

|Evento|Descripción|
|---|---|
|PositionOpened|Nueva posición creada|
|PositionUpdated|Posición modificada|
|PositionClosed|Posición cerrada|

---

# 5. Agregados principales

## [[Strategy]]

Representa una lógica de trading.

Contiene:

- Nombre.
- Mercado objetivo.
- Indicadores.
- Reglas de entrada.
- Reglas de salida.
- Estado.

---

## [[Order]]

Representa una instrucción enviada al exchange.

Contiene:

- Tipo de orden.
- Símbolo.
- Cantidad.
- Precio.
- Estado.

---

## [[Position]]

Representa una exposición activa del portfolio.

Contiene:

- Símbolo.
- Dirección.
- Tamaño.
- Precio de entrada.
- Precio actual.
- PnL.

---

## [[Portfolio]]

Representa la gestión del capital.

Contiene:

- Balance.
- Capital disponible.
- Posiciones activas.
- Historial.

---

## RiskProfile

Representa las reglas de protección del capital.

Contiene:

- Riesgo máximo por operación.
- Stop loss.
- Take profit.
- Exposición máxima.

---

# 6. Bounded Context inicial

## [[Strategy]] Management

Responsable de crear y administrar estrategias.

---

## [[Market]] Data

Responsable de obtener y procesar información del mercado.

---

## [[Signal]] Generation

Responsable de detectar oportunidades de entrada o salida.

---

## [[Order]] Execution

Responsable de comunicarse con exchanges.

---

## [[Portfolio]] Management

Responsable del seguimiento de capital y posiciones.

---

## Risk Management

Responsable de validar operaciones.

---

# Estado

Versión inicial del modelo de dominio.

Pendiente:

- Refinar agregados.
- Definir entidades.
- Crear relaciones.
- Diseñar arquitectura basada en bounded contexts.
