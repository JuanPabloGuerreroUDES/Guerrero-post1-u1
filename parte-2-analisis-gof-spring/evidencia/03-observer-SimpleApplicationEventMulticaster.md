# Evidencia — Patrón Observer

- **Clase:** `org.springframework.context.event.SimpleApplicationEventMulticaster`
- **Interfaz observada:** `org.springframework.context.ApplicationListener`
- **Módulo:** `spring-context`
- **Repositorio:** [spring-projects/spring-framework](https://github.com/spring-projects/spring-framework)
- **Ruta:** `spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java`
- **Rama consultada:** `main` (agosto de 2026)

## Declaración de la clase

```java
package org.springframework.context.event;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    // ...
}
```

**Comentario del estudiante:** `AbstractApplicationEventMulticaster` mantiene
internamente la colección de "observadores" registrados (los `ApplicationListener`
suscritos al contexto de Spring). `SimpleApplicationEventMulticaster` es el
"sujeto" (subject/publisher) concreto que recorre esa colección y notifica a
cada observador cuando ocurre un evento.

## Método `multicastEvent`

```java
@Override
public void multicastEvent(ApplicationEvent event) {
    multicastEvent(event, null);
}

@Override
public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
    ResolvableType type = (eventType != null ? eventType : ResolvableType.forInstance(event));
    Executor executor = getTaskExecutor();
    // Recorre a TODOS los listeners suscritos a este tipo de evento
    for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
        if (executor != null && listener.supportsAsyncExecution()) {
            try {
                executor.execute(() -> invokeListener(listener, event));
            }
            catch (RejectedExecutionException ex) {
                invokeListener(listener, event);
            }
        }
        else {
            // Notificación síncrona: invoca listener.onApplicationEvent(event)
            invokeListener(listener, event);
        }
    }
}
```

**Comentario del estudiante:** `multicastEvent` es la operación clásica
"notify()" del patrón Observer: por cada observador registrado
(`ApplicationListener`) que sea compatible con el tipo de evento publicado,
se invoca su método `onApplicationEvent(event)` (directamente en el mismo hilo,
o en un `Executor` si el listener soporta ejecución asíncrona). Quien publica
el evento (`ApplicationEventPublisher.publishEvent(...)`) no conoce ni depende
de los listeners concretos que reaccionarán a él.

**Fuente:** Spring Framework (código fuente), rama `main`. Recuperado de
https://github.com/spring-projects/spring-framework/blob/main/spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java
