# Evidencia — Patrón Singleton

- **Clase:** `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry`
- **Módulo:** `spring-beans`
- **Repositorio:** [spring-projects/spring-framework](https://github.com/spring-projects/spring-framework)
- **Ruta:** `spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java`
- **Rama consultada:** `main` (agosto de 2026)

## Declaración de la clase y del caché de instancias

```java
package org.springframework.beans.factory.support;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry
        implements SingletonBeanRegistry {

    /** Cache of singleton objects: bean name to bean instance. */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // ...
}
```

**Comentario del estudiante:** `singletonObjects` es un `ConcurrentHashMap` que actúa
como el registro central de instancias únicas por nombre de bean. Es la estructura
de datos que materializa el patrón: en lugar de que cada punto del framework cree su
propia instancia, todas consultan este mismo mapa compartido.

## Método `getSingleton(String, ObjectFactory<?>)`

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    Assert.notNull(beanName, "Bean name must not be null");

    Thread currentThread = Thread.currentThread();
    Boolean lockFlag = isCurrentThreadAllowedToHoldSingletonLock();
    boolean acquireLock = !Boolean.FALSE.equals(lockFlag);
    boolean locked = (acquireLock && this.singletonLock.tryLock());

    try {
        // 1) Si la instancia ya existe en el caché, se retorna directamente
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            // 2) Si no existe, se invoca la ObjectFactory UNA sola vez
            //    y el resultado se registra en el caché antes de devolverlo
            //    (lógica de bloqueo y manejo de referencias circulares omitida aquí)
        }
        return singletonObject;
    }
    finally {
        // liberación del bloqueo
    }
}
```

**Comentario del estudiante:** el método sigue exactamente la estructura clásica
de un Singleton perezoso ("lazy"): primero consulta el caché (`singletonObjects.get`)
y solo si no encuentra la instancia, la crea a través de la `ObjectFactory` recibida
y la almacena antes de devolverla. Todas las llamadas posteriores para el mismo
`beanName` devuelven la misma referencia de objeto. El uso de `singletonLock`
garantiza que la creación sea segura en entornos multihilo.

**Fuente:** Spring Framework (código fuente), rama `main`. Recuperado de
https://github.com/spring-projects/spring-framework/blob/main/spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java
