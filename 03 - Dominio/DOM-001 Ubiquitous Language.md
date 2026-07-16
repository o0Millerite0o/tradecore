**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 15/07/2026 
**Última actualización:** 15/07/2026 
**Autor:** Miler Ayarza 

---

## Objetivo 

"Este documento define el lenguaje común del dominio de TradeCore. Todas las decisiones funcionales y técnicas deberán alinearse con estas definiciones."
## Alcance 

• Trading algorítmico
• Gestión de estrategias
• Gestión de órdenes
• Gestión del riesgo
• Mercado
• Portafolios
• Ejecución
## Glosario 
| Término             | Definición                                                                                  |
| ------------------- | ------------------------------------------------------------------------------------------- |
| [[Asset]]           | Activo financiero negociable, por ejemplo BTC, ETH, SOL o una acción.                       |
| [[Market]]          | Lugar donde se negocian los activos, como LBank, Binance o Bybit.                           |
| [[Trading Pair]]    | Par de intercambio entre dos activos, por ejemplo BTC/USDT.                                 |
| [[Order]]           | Solicitud enviada al mercado para comprar o vender un activo.                               |
| [[Trade]]           | Ejecución total o parcial de una orden.                                                     |
| [[Position]]        | Resultado de una o varias ejecuciones que representan una exposición abierta en el mercado. |
| [[Portfolio]]       | Conjunto de posiciones y balances administrados por el usuario.                             |
| [[Balance]]         | Cantidad disponible de un activo dentro de una cuenta.                                      |
| [[Strategy]]        | Conjunto de reglas que decide cuándo abrir o cerrar posiciones.                             |
| [[Signal]]          | Evento generado por una estrategia indicando una oportunidad de compra o venta.             |
| [[Risk Management]] | Reglas que limitan pérdidas y controlan la exposición del capital.                          |
| [[Exchange]]        | Plataforma donde se ejecutan las órdenes, como LBank.                                       |
| [[Candle]]          | Representación OHLCV de un intervalo de tiempo del mercado.                                 |
| [[Timeframe]]       | Duración de cada vela, por ejemplo 1m, 5m, 1H o 4H.                                         |
## Reglas de lenguaje

[[Order]] ≠ [[Trade]]
[[Trade]] ≠ [[Position]]
[[Signal]] ≠ [[Order]]
[[Strategy]] ≠ [[Bot]]
[[Portfolio]] ≠ [[Wallet]]

## Términos prohibidos

"Operación"

No indica si hablamos de:
• una orden,
• un trade,
• una posición,
• una estrategia.

En el código evitaremos palabras genéricas como:

Data
Info
Manager
Helper
Processor
Util
Object
Bean
Entity (cuando no sea una entidad JPA)

## Decisiones de modelado iniciales

Desde este momento propongo adoptar estas definiciones como estándar del proyecto:

• Una [[Strategy]] genera Signals.
• Un [[Signal]] solicita la creación de una [[Order]].
• Una [[Order]] es enviada al [[Exchange]].
• El [[Exchange]] responde con uno o varios [[Trade]].
• Los [[Trade]] actualizan una [[Position]].
• Las [[Position]] forman parte de un [[Portfolio]].

Este flujo será la columna vertebral del dominio y se reflejará más adelante en los casos de uso, el modelo de dominio y la arquitectura.

## Entregable de hoy

Al finalizar DOM-001 tendremos:

- Un glosario oficial del dominio.
- Definiciones sin ambigüedades para los conceptos principales.
- Reglas de nomenclatura que guiarán el desarrollo.
- Un flujo conceptual del ciclo de vida de una operación de trading.

Con este documento evitaremos problemas de diseño desde el inicio y tendremos una base sólida para la siguiente etapa.

## Próximo paso

Una vez aprobado DOM-001, continuaremos con:

**DOM-002 - Event Storming del dominio**

Ahí identificaremos los eventos de negocio, comandos, actores, agregados y límites del dominio. Ese ejercicio será la base para definir los Bounded Contexts y, posteriormente, la arquitectura de TradeCore.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 15/07/2026 | Miler Ayarza | Creación del documento |
