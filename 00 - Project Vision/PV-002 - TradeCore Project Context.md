

## 1. Visión del proyecto

TradeCore es una plataforma profesional de trading algorítmico diseñada para analizar mercados financieros, ejecutar estrategias automatizadas y gestionar operaciones de trading.

El objetivo no es crear un script simple de compra y venta, sino construir una plataforma con arquitectura profesional aplicando principios utilizados en equipos backend senior:

- Domain Driven Design (DDD).
    
- Arquitectura Hexagonal.
    
- Modular Monolith como primera etapa.
    
- SOLID.
    
- Clean Code.
    
- Testing automatizado.
    
- Observabilidad.
    
- Integración con APIs externas.
    
- Gestión de riesgo.
    
- Diseño orientado a evolución hacia microservicios si el crecimiento lo requiere.
    

El proyecto tiene dos objetivos:

1. Crear una herramienta funcional de trading algorítmico.
    
2. Servir como proyecto profesional para demostrar capacidades de Backend Java Semi Senior.
    

---

# 2. Estado actual del proyecto

## Infraestructura completada

### Desarrollo

- Sistema operativo:
    
    - macOS.
        
- IDE:
    
    - IntelliJ IDEA Ultimate.
        
- Lenguaje:
    
    - Java 21.
        
- Framework:
    
    - Spring Boot 4.1.
        
- Build:
    
    - Maven.
        

---

## Base de datos

Motor:

- PostgreSQL 17.
    

Configuración creada:

Base de datos:

```
tradecore
```

Usuario:

```
tradecore_user
```

La aplicación ya logró conectarse correctamente mediante Spring Boot.

---

## Backend actual

Dependencias configuradas:

- Spring Data JPA.
    
- Hibernate.
    
- Flyway.
    
- Validation.
    
- Actuator.
    
- Web MVC.
    
- PostgreSQL Driver.
    
- Lombok.
    
- Testcontainers.
    

La aplicación inicia correctamente.

Estado:

```
Spring Boot Started
Database Connection OK
Flyway Initialized
Hibernate Initialized
```

---

# 3. Control de versiones

Repositorio:

GitHub.

Configuración:

- Git SSH configurado.
    
- Push funcionando.
    
- Branch principal:
    

```
main
```

Últimos cambios versionados:

- Project Vision.
    
- ADR Java 21.
    
- ADR PostgreSQL.
    
- ADR Modular Monolith Architecture.
    
- ADR Hexagonal Architecture.
    
- Configuración inicial Spring Boot.
    
- Configuración PostgreSQL.
    

---

# 4. Documentación del proyecto

Se utiliza Obsidian como herramienta de documentación.

Estructura:

```
TradeCore

00 - Project Vision

01 - Roadmap

02 - ADR

03 - Dominio

04 - Casos de Uso

05 - Arquitectura

06 - Bases de Datos

07 - APIs

08 - Integraciones

09 - Trading Engine

10 - Code Reviews

11 - Lecciones Aprendidas
```

---

# 5. Decisiones arquitectónicas tomadas

## ADR-001 Java 21

Motivo:

Utilizar una versión LTS moderna de Java.

Beneficios:

- Mejoras del lenguaje.
    
- Mejor rendimiento.
    
- Mayor tiempo de soporte.
    
- Preparación para entornos empresariales.
    

---

## ADR-002 PostgreSQL

Motivo:

Elegido como base principal por:

- Robustez.
    
- Soporte para datos financieros.
    
- Funciones avanzadas.
    
- Comunidad empresarial.
    
- Mejor adaptación a sistemas con alta consistencia.
    

---

## ADR-003 Modular Monolith

Primera arquitectura.

Motivo:

Evitar complejidad prematura.

El sistema estará dividido por módulos internos:

Ejemplo:

```
tradecore

├── account
├── market
├── strategy
├── trading
├── risk
├── notification
└── reporting
```

La separación permitirá evolucionar hacia microservicios si el negocio lo requiere.

---

## ADR-004 Arquitectura Hexagonal

Objetivo:

Separar:

Dominio.

Aplicación.

Infraestructura.

Ejemplo:

```
domain

application

infrastructure

interfaces
```

El dominio no debe depender de frameworks externos.

---

# 6. Metodología de diseño

El proyecto seguirá:

```
Lenguaje Ubicuo
        ↓
Event Storming
        ↓
Modelo de Dominio
        ↓
Casos de Uso
        ↓
Arquitectura
        ↓
Código
        ↓
Pruebas
```

Actualmente:

Completado:

- DOM-001 Ubiquitous Language.
    

En progreso:

- DOM-002 Event Storming.
    

---

# 7. Objetivo profesional

Este proyecto busca desarrollar habilidades equivalentes a un Backend Developer Semi Senior.

Áreas que se deben dominar durante el desarrollo:

## Java avanzado

Conocimientos esperados:

- Programación orientada a objetos avanzada.
    
- Streams.
    
- Optional.
    
- Records.
    
- Enums.
    
- Concurrencia.
    
- CompletableFuture.
    
- JVM.
    
- Garbage Collector.
    
- Performance.
    

---

## Spring Boot profesional

Dominar:

- Dependency Injection.
    
- Auto Configuration.
    
- Profiles.
    
- Security.
    
- Transactions.
    
- Events.
    
- Scheduling.
    
- Async Processing.
    
- Actuator.
    
- Configuration Management.
    

---

## Persistencia

Dominar:

- JPA/Hibernate.
    
- Relaciones.
    
- Lazy vs Eager.
    
- Entity Lifecycle.
    
- Query Optimization.
    
- Índices.
    
- Migraciones Flyway.
    
- Diseño relacional.
    

---

## Arquitectura

Aprender:

- DDD.
    
- Hexagonal Architecture.
    
- Clean Architecture.
    
- SOLID.
    
- Design Patterns.
    
- Event Driven Architecture.
    
- Modular Design.
    

---

# 8. Producto final esperado

TradeCore tendrá módulos:

## Market Data

Responsable de obtener información del mercado.

Fuentes posibles:

- Binance API.
    
- Coinbase API.
    
- TradingView.
    
- CoinGecko.
    
- CoinMarketCap.
    
- APIs financieras tradicionales.
    

Datos:

- Precio.
    
- Volumen.
    
- Velas OHLCV.
    
- Indicadores técnicos.
    

---

## Trading Engine

Motor principal.

Responsabilidades:

- Ejecutar estrategias.
    
- Evaluar señales.
    
- Gestionar órdenes.
    
- Simular operaciones.
    

---

## Strategy Engine

Permite crear estrategias:

Ejemplos:

- Moving Average Cross.
    
- RSI.
    
- MACD.
    
- Breakout.
    
- Mean Reversion.
    

---

## Risk Management

Controlará:

- Tamaño de posición.
    
- Stop Loss.
    
- Take Profit.
    
- Máxima pérdida diaria.
    
- Exposición por activo.
    

---

## Order Management

Gestionará:

- Órdenes.
    
- Estados.
    
- Ejecuciones.
    
- Historial.
    

---

## Portfolio

Controlará:

- Balance.
    
- Activos.
    
- Rendimiento.
    
- Pérdidas y ganancias.
    

---

# 9. Herramientas futuras

## Backend

- Java 21.
    
- Spring Boot.
    
- PostgreSQL.
    
- Flyway.
    
- Redis.
    
- Kafka.
    
- Docker.
    
- Kubernetes opcional.
    

---

## Testing

- JUnit 5.
    
- Mockito.
    
- Testcontainers.
    
- Integration Testing.
    
- Contract Testing.
    

---

## Observabilidad

- Spring Actuator.
    
- Prometheus.
    
- Grafana.
    
- Loki.
    
- OpenTelemetry.
    

---

## DevOps

- GitHub Actions.
    
- Jenkins.
    
- Docker.
    
- CI/CD.
    

---

# 10. Inteligencia artificial aplicada

La IA tendrá funciones como:

## Analista de mercado

Analizar:

- Noticias.
    
- Sentimiento.
    
- Datos históricos.
    
- Indicadores.
    

---

## Asistente de estrategias

Ayudar a:

- Diseñar estrategias.
    
- Evaluar riesgos.
    
- Analizar resultados.
    

---

## Machine Learning futuro

Posibles aplicaciones:

- Predicción de volatilidad.
    
- Clasificación de escenarios.
    
- Detección de patrones.
    

---

# 11. Información de negocio necesaria

Para construir correctamente el sistema se debe entender:

## Mercados financieros

- Criptomonedas.
    
- Forex.
    
- Acciones.
    
- ETFs.
    

---

## Conceptos trading

- Order Book.
    
- Bid.
    
- Ask.
    
- Spread.
    
- Liquidez.
    
- Market Order.
    
- Limit Order.
    
- Stop Loss.
    
- Take Profit.
    
- Leverage.
    
- Futures.
    

---

## Métricas

- ROI.
    
- Drawdown.
    
- Win Rate.
    
- Sharpe Ratio.
    
- Profit Factor.
    

---

# 12. Forma de trabajo esperada de la IA

Claude y ChatGPT deben actuar como asistentes técnicos del proyecto.

Las respuestas deben considerar:

- Arquitectura actual.
    
- ADR existentes.
    
- Decisiones tomadas.
    
- Lenguaje ubicuo.
    
- Objetivo profesional Semi Senior.
    

Evitar:

- Código rápido sin diseño.
    
- Soluciones aisladas.
    
- Cambios arquitectónicos sin ADR.
    
- Complejidad innecesaria.
    

Cada cambio importante debe considerar:

1. Problema.
    
2. Diseño.
    
3. Decisión.
    
4. Implementación.
    
5. Prueba.
    
6. Documentación.
    

---

# 13. Meta final

Al finalizar TradeCore se espera tener:

- Una plataforma funcional de trading algorítmico.
    
- Código profesional en Java.
    
- Arquitectura documentada.
    
- Pruebas automatizadas.
    
- Integraciones reales.
    
- Experiencia práctica equivalente a proyectos backend empresariales.
    
- Portfolio demostrable para posiciones Backend Java Semi Senior.