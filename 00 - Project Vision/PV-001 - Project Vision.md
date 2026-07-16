
**Versión:** 1.0
**Estado:** En elaboración
**Fecha de creación:** 15/07/2026
**Última actualización:** 15/07/2026
**Autor:** Miler Ayarza
**Revisor:** ChatGPT (Tutor Java Backend)

---

## Objetivo
Diseñar y desarrollar una plataforma profesional de trading algorítmico que permita automatizar [[Strategy]] de inversión en distintos [[Exchanges]] de criptomonedas.

La plataforma deberá evolucionar desde un entorno de pruebas (Paper Trading) hasta un entorno de producción con operaciones reales, únicamente cuando existan suficientes evidencias de estabilidad y seguridad.

Este proyecto también será utilizado como laboratorio para aprender Java Backend Development y aplicar buenas prácticas de arquitectura y diseño de software.
## Contexto
Actualmente existe un prototipo desarrollado en Python que permite ejecutar pruebas de [[Strategy]] sobre LBank.

Sin embargo, el objetivo del proyecto es rediseñar completamente la solución utilizando Java y Spring Boot para construir una plataforma escalable, mantenible y preparada para soportar múltiples [[Exchanges]] y [[Strategy]].
## Alcance
La primera versión deberá permitir:

- Obtener datos de mercado.
- Conectarse a LBank.
- Ejecutar Paper Trading.
- Registrar operaciones.
- Exponer una API REST.
- Persistir información.

Lo que no hará:

- IA.
- Múltiples [[Exchanges]].
- Alta disponibilidad.
- Trading con dinero real.

Eso vendrá después.
## Objetivos del proyecto

✔ Construir una arquitectura limpia.
✔ Aplicar SOLID.
✔ Diseñar una plataforma escalable.
✔ Integrar APIs REST.
✔ Registrar todas las operaciones.
✔ Permitir agregar nuevos [[Exchanges]].
✔ Permitir agregar nuevas [[Strategy]].
## Objetivos del aprendizaje

Aprender Java moderno.
Dominar Programación Orientada a Objetos.
Dominar Spring Boot.
Aprender Arquitectura Hexagonal.
Aprender Docker.
Aprender PostgreSQL.
Aprender Testing.
Aprender integración con APIs.
Aprender diseño de software.
Prepararme para posiciones Java Backend Semi Senior.
## Tecnologías 

Java 21
Spring Boot
PostgreSQL
Docker
Flyway
Redis
RabbitMQ (futuro)
JUnit
Mockito
Git
GitHub
Maven
## Restricciones 
No operar con dinero real hasta estar seguro que el bot está listo para hacerlo.
## Riesgos

- Integración con APIs externas.
- Cambios en la API de LBank.
- Errores en estrategias.
- Pérdidas económicas.
- Baja cobertura de pruebas.
- Problemas de concurrencia.
- Latencia de red.
- Errores humanos.
## Glosario
## Próximos pasos
---

# Historial de cambios

| Versión | Fecha      | Autor        | Descripción            |
| ------- | ---------- | ------------ | ---------------------- |
| 1.0     | 15/07/2026 | Miler Ayarza | Creación del documento |
