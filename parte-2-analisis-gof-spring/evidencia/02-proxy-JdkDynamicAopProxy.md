# Evidencia — Patrón Proxy

- **Clase:** `org.springframework.aop.framework.JdkDynamicAopProxy`
- **Módulo:** `spring-aop`
- **Repositorio:** [spring-projects/spring-framework](https://github.com/spring-projects/spring-framework)
- **Ruta:** `spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java`
- **Rama consultada:** `main` (agosto de 2026)

## Declaración de la clase

```java
package org.springframework.aop.framework;

final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {
    // ...
}
```

**Comentario del estudiante:** la clase implementa `InvocationHandler` de la API de
proxies dinámicos de Java (`java.lang.reflect`). Esto le permite interceptar
cualquier llamada a método hecha sobre el objeto proxy y decidir qué hacer antes
de (o en lugar de) delegar al objeto real (`target`).

## Método `getProxy(ClassLoader)`

```java
@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
    if (logger.isTraceEnabled()) {
        logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
    }
    return Proxy.newProxyInstance(
            determineClassLoader(classLoader), this.cache.proxiedInterfaces, this);
}
```

**Comentario del estudiante:** `Proxy.newProxyInstance` construye en tiempo de
ejecución un objeto que implementa las mismas interfaces que el objeto real
(`proxiedInterfaces`), pero cuyas llamadas son enrutadas hacia `this`
(la propia instancia de `JdkDynamicAopProxy`, que es el `InvocationHandler`).

## Método `invoke(Object, Method, Object[])`

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    TargetSource targetSource = this.advised.targetSource;
    Object target = null;
    try {
        // ... comprobaciones de equals()/hashCode()/interfaz Advised ...

        Object retVal;
        target = targetSource.getTarget();
        Class<?> targetClass = (target != null ? target.getClass() : null);

        // Obtiene la cadena de interceptores/advices configurados para este método
        List<Object> chain =
                this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        if (chain.isEmpty()) {
            // Sin advices aplicables: se invoca el método directamente sobre el target
            retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
        }
        else {
            // Con advices: se construye una cadena de invocación (logging,
            // transacciones, seguridad, etc.) que se ejecuta ANTES de llegar al target
            MethodInvocation invocation =
                    new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
            retVal = invocation.proceed();
        }
        return retVal;
    }
    finally {
        if (target != null && !targetSource.isStatic()) {
            targetSource.releaseTarget(target);
        }
    }
}
```

**Comentario del estudiante:** este es el punto central del patrón. Cada llamada
a un método del bean "proxificado" pasa primero por `invoke()`, que puede ejecutar
lógica adicional (transacciones, seguridad, logging, caché) antes de delegar —o no—
la llamada al objeto real (`target`) mediante `invocation.proceed()`. El cliente
que usa el bean nunca sabe que está hablando con un proxy y no con el objeto real.

**Fuente:** Spring Framework (código fuente), rama `main`. Recuperado de
https://github.com/spring-projects/spring-framework/blob/main/spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java
