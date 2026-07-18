**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 17/07/2026 
**Última actualización:** 17/07/2026 
**Autor:** Miler Ayarza 

---

## Estado

Accepted

---

## Fecha

2026-07-17

---

## Contexto

ADR-003 definió que TradeCore será un Modular Monolith con seis módulos, uno por cada Bounded Context de DOM-003 ([[Strategy]] Management, [[Market]] Data, [[Signal]] Generation, Risk Management, [[Order]] Execution, [[Portfolio]] Management) más un Shared Kernel, y estableció como regla de diseño que "las reglas de negocio nunca dependerán del framework".

Falta definir cómo se organiza el código **dentro** de cada módulo para cumplir esa regla en la práctica: que el dominio (Strategy, Order, Position, Portfolio — DOM-004/DOM-006) no dependa de Spring, JPA, ni de ningún exchange concreto.

---

## Decisión

Cada módulo se organiza siguiendo Arquitectura Hexagonal (Ports & Adapters), con cuatro paquetes:

- `domain`
- `application`
- `infrastructure`
- `interfaces`

---

## Estructura de paquetes (ejemplo: módulo strategy)

```
com.tradecore.strategy
├── domain
│   ├── Strategy.java                 (Aggregate Root, DOM-006)
│   ├── StrategyRule.java             (Value Object)
│   ├── StrategyStatus.java           (Enum, DOM-005)
│   ├── event
│   │   ├── StrategyCreated.java
│   │   ├── StrategyActivated.java
│   │   └── StrategyDeactivated.java
│   └── StrategyRepository.java       (Port — interfaz, sin implementación)
│
├── application
│   ├── CreateStrategyUseCase.java
│   ├── UpdateStrategyUseCase.java
│   ├── ActivateStrategyUseCase.java
│   └── DeactivateStrategyUseCase.java
│
├── infrastructure
│   ├── persistence
│   │   ├── StrategyJpaEntity.java
│   │   ├── StrategyJpaRepository.java      (Spring Data JPA)
│   │   └── StrategyRepositoryAdapter.java  (implementa el Port de domain)
│   └── event
│       └── SpringStrategyEventPublisher.java
│
└── interfaces
    └── rest
        ├── StrategyController.java
        └── dto
            ├── CreateStrategyRequest.java
            └── StrategyResponse.java
```

El mismo patrón se repite en `marketdata`, `signal`, `risk`, `order` (Order Execution) y `portfolio`. No todos los módulos tendrán un Aggregate Root en `domain`: por ejemplo, `signal` y `risk` son más bien servicios de dominio sin estado persistente propio (Signal no es Entity ni Aggregate — DOM-007; Risk Manager evalúa y responde, no necesariamente persiste una Entity).

El **Shared Kernel** es la excepción: no sigue las cuatro capas, es una librería de building blocks reutilizados por los demás módulos.

```
com.tradecore.shared.domain
├── Price.java
├── Quantity.java
├── Percentage.java
├── TradingPair.java
├── PnL.java
└── DomainEvent.java   (interfaz/record base, DOM-007 sección 2)
```

---

## Reglas de dependencia entre capas (dentro de un módulo)

- `domain` no depende de `application`, `infrastructure` ni `interfaces`. No importa nada de Spring ni de JPA.
- `application` depende de `domain` (usa sus Ports), pero nunca importa clases de `infrastructure` ni de `interfaces` directamente.
- `infrastructure` implementa los Ports que `domain` define (ej. `StrategyRepository`); depende de `domain`, nunca al revés.
- `interfaces` depende de `application` (invoca los Use Cases); no accede directo a `domain` ni a `infrastructure`.
- Entre módulos distintos: solo por eventos de dominio o por un contrato público explícito — nunca importando clases internas de `domain` de otro módulo (regla ya fijada en ADR-003).
- Aunque `marketdata` y `order` hablen ambos con el mismo exchange (ej. LBank), cada uno define su propio Port/Adapter — no comparten cliente de infraestructura, para no acoplar un módulo a otro a través de un detalle técnico.

---

## Consecuencias

### Positivas

- El dominio se puede testear sin levantar el contexto de Spring.
- Cambiar de PostgreSQL a otro motor, o de un exchange a otro, no toca una sola clase de `domain`.
- Si un módulo necesita extraerse a microservicio (criterios de ADR-003), su lógica de negocio ya está aislada y no hay que reescribirla.

### Negativas

- Más clases por funcionalidad pequeña (mapeo entre capas: Entity de dominio ↔ Entity JPA ↔ DTO).
- Curva de aprendizaje inicial más alta que escribir todo en un `@RestController` con JPA directo.

---

## Decisiones futuras relacionadas

- ADR-005 Comunicación basada en eventos.
- ADR-006 Persistencia por módulo.

---

## Estado

Accepted.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 17/07/2026 | Miler Ayarza | Creación del documento |
