**Versión:** 1.0 
**Estado:** En elaboración 
**Fecha de creación:** 16/07/2026 
**Última actualización:** 16/07/2026 
**Autor:** Miler Ayarza 

---

## Estado

Accepted

---

## Fecha

2026-07-16

---

## Contexto

TradeCore es una plataforma de trading algorítmico diseñada bajo principios de Domain-Driven Design (DDD).

Desde el inicio del proyecto se identificaron múltiples Bounded Contexts:

- [[Strategy]] Management
- [[Market]] Data
- [[Signal]] Generation
- Risk Management
- [[Order]] Execution
- [[Portfolio]] Management

La decisión consiste en determinar si la primera versión debe implementarse como:

- Monolito modular.
- Microservicios.

---

## Decisión

La primera versión de TradeCore se implementará como un Modular Monolith.

Cada Bounded Context será implementado como un módulo independiente dentro de una única aplicación Spring Boot.

La comunicación entre módulos será mediante interfaces y eventos internos del dominio.

No se permitirá el acceso directo entre módulos a través de entidades o repositorios.

---

## Justificación

### 1. Menor complejidad

Un único despliegue simplifica el desarrollo, las pruebas y la operación.

### 2. Mayor velocidad de desarrollo

El equipo puede enfocarse en construir el dominio sin invertir tiempo en infraestructura distribuida.

### 3. Mejor experiencia de depuración

Es posible seguir una ejecución completa con un solo depurador y un único proceso.

### 4. Costos reducidos

No es necesario administrar múltiples servicios, balanceadores, mensajería ni observabilidad distribuida.

### 5. Evolución controlada

Los límites del dominio seguirán definidos mediante DDD, permitiendo extraer módulos a microservicios cuando exista una necesidad real.

---

## Arquitectura propuesta

```
                 TradeCore

        Spring Boot Application

+-------------------------------------------+

 Strategy Management

 Market Data

 Signal Generation

 Risk Management

 Order Execution

 Portfolio Management

 Shared Kernel

+-------------------------------------------+
```

Cada módulo mantiene su propio modelo de dominio, servicios de aplicación e infraestructura.

---

## Reglas de diseño

### Independencia

Cada módulo debe ser autónomo.

### Bajo acoplamiento

Los módulos solo interactúan mediante contratos públicos.

### Alta cohesión

Toda la lógica relacionada debe permanecer dentro del mismo módulo.

### Sin dependencias circulares

No se permiten referencias mutuas entre módulos.

### Dominio primero

Las reglas de negocio nunca dependerán del framework.

---

## Comunicación entre módulos

Permitido:

- Interfaces.
- Eventos del dominio.
- Servicios de aplicación.

No permitido:

- Acceso directo a repositorios de otro módulo.
- Manipulación de entidades externas.
- Dependencias de infraestructura entre módulos.

---

## Criterios para migrar a microservicios

Un módulo podrá convertirse en microservicio cuando cumpla uno o más de los siguientes criterios:

- Escalabilidad independiente.
- Alta carga de procesamiento.
- Ciclo de despliegue independiente.
- Requerimientos de disponibilidad distintos.
- Integraciones externas dedicadas.

---

## Consecuencias

### Positivas

- Desarrollo más rápido.
- Arquitectura limpia.
- Fácil mantenimiento.
- Menor costo operativo.
- Base sólida para evolucionar.

### Negativas

- Escalabilidad limitada al proceso.
- Despliegue único.
- Compartición de recursos de JVM.

---

## Decisiones futuras relacionadas

- ADR-004 Arquitectura Hexagonal.
- ADR-005 Comunicación basada en eventos.
- ADR-006 Persistencia por módulo.

---

## Estado

Accepted.

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 16/07/2026 | Miler Ayarza | Creación del documento |
