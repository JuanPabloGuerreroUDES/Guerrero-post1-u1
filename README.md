# Guerrero-post1-u1

Post-contenido — Refactorización SOLID y análisis de patrones GoF en Spring

## Descripción

Repositorio del post-contenido de la Unidad 1 de Patrones de Diseño de
Software. Contiene dos partes: refactorización SOLID de un God Object
(`parte-1-refactorizacion-solid/`) y análisis de patrones GoF en Spring
Framework (`parte-2-analisis-gof-spring/`).

## Parte 1 — Refactorización SOLID

Proyecto Maven que refactoriza la clase `OrderProcessor` (un God Object
que concentraba lógica de negocio, persistencia, notificación y
presentación) aplicando los principios SRP, OCP y DIP.

### Análisis de Violaciones SOLID

| Principio | Método/Sección afectada | Descripción de la violación |
|-----------|--------------------------|------------------------------|
| SRP | `calculateTotal` + `applyDiscount` + `saveOrder` + `sendEmail` + `printReport` | `OrderProcessor` concentra cinco responsabilidades no relacionadas (cálculo de impuestos, aplicación de descuentos, persistencia, notificación y presentación) en una misma clase. Cualquier cambio en la lógica de negocio, en el mecanismo de almacenamiento, en el canal de notificación o en el formato del reporte obliga a modificar la misma unidad de código, lo que aumenta el acoplamiento y el riesgo de que un cambio en una responsabilidad rompa otra que no tiene relación con ella. |
| OCP | `applyDiscount` (if/else sobre `customerType`) | El método decide el descuento mediante una cadena de condicionales `if/else` sobre un `String`. Agregar un nuevo tipo de cliente (por ejemplo `"PREMIUM"` o `"MAYORISTA"`) exige editar el cuerpo del método ya existente y probado, en lugar de extender el comportamiento agregando código nuevo. Esto viola el principio de que las entidades de software deben estar abiertas a extensión pero cerradas a modificación. |
| DIP | Toda la clase (dependencias internas sin abstracciones) | `OrderProcessor` no depende de abstracciones: la persistencia (una `List<String>` en memoria con salida por consola simulando una base de datos) y la notificación (impresión en consola simulando un envío de correo) están implementadas directamente dentro de la clase, sin interfaces que las representen. El módulo de alto nivel (el procesamiento de la orden) depende así de detalles de bajo nivel concretos en lugar de depender de abstracciones, impidiendo sustituir o simular esos componentes sin modificar la clase completa. |

### Refactorización aplicada

- **SRP** — Las responsabilidades se separaron en `TaxCalculator`, `OrderRepository`, `EmailNotifier` y `OrderReporter`, cada una con una única razón para cambiar.
- **OCP** — Se extrajo la interfaz `DiscountStrategy` con las implementaciones `VipDiscount`, `RegularDiscount` y `NoDiscount`, permitiendo agregar nuevos tipos de descuento sin modificar código existente.
- **DIP** — `OrderService` depende únicamente de abstracciones (`TaxCalculator`, `OrderRepository`, `EmailNotifier`, `DiscountStrategy`) inyectadas por constructor, en lugar de crear sus propias dependencias concretas.

### Cómo ejecutar

```bash
cd parte-1-refactorizacion-solid
mvn compile
mvn exec:java -Dexec.mainClass="com.patrones.u1.Main"
```

`Main.java` instancia `OrderService` con dos estrategias de descuento
distintas (VIP y Regular), procesa dos órdenes y finalmente imprime el
reporte de las órdenes guardadas en el repositorio en memoria.

Ver [parte-1-refactorizacion-solid/](parte-1-refactorizacion-solid/).

## Parte 2 — Análisis de Patrones GoF en Spring

Investigación sobre el código fuente de Spring Framework
(`spring-projects/spring-framework` en GitHub) que identifica y analiza tres
patrones GoF de categorías distintas, conectando cada uno con el problema
concreto que resuelve en Spring Boot y con el principio SOLID que refuerza.

| # | Patrón | Categoría | Clase en Spring |
|---|--------|-----------|-----------------|
| 1 | Singleton | Creacional | `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry` (spring-beans) |
| 2 | Proxy | Estructural | `org.springframework.aop.framework.JdkDynamicAopProxy` (spring-aop) |
| 3 | Observer | Comportamiento | `org.springframework.context.event.SimpleApplicationEventMulticaster` (spring-context) |

Ver [parte-2-analisis-gof-spring/documento-analisis.md](parte-2-analisis-gof-spring/documento-analisis.md)
y los extractos de código en
[parte-2-analisis-gof-spring/evidencia/](parte-2-analisis-gof-spring/evidencia/).

## Herramientas utilizadas

- Java 17, Apache Maven, VS Code, Git, GitHub
- Código fuente de Spring Framework (investigación)

## Conclusiones

Este post-contenido evidenció que los principios SOLID y los patrones de
diseño GoF son dos caras de la misma disciplina: al refactorizar
`OrderProcessor` en la Parte 1, la separación de responsabilidades (SRP), la
extracción de `DiscountStrategy` (OCP, patrón Strategy) y la inyección de
dependencias por constructor (DIP) produjeron un diseño más flexible y
comprobable con una simple clase `Main`. Al investigar el código fuente de
Spring Framework en la Parte 2 se confirmó que ese mismo razonamiento opera a
escala de framework: Singleton, Proxy y Observer no son ejercicios teóricos,
sino soluciones reales que Spring usa para gestionar el ciclo de vida de los
beans, aplicar comportamiento transversal sin acoplar la lógica de negocio, y
desacoplar la publicación de eventos de sus consumidores. La lección más
importante para el diseño propio es que un patrón GoF se adopta cuando
resuelve un problema genuino de acoplamiento o extensibilidad —nunca como
una decisión prematura o estética— y que reconocerlos en frameworks maduros
ayuda a diseñar con el mismo nivel de intención en proyectos propios.
