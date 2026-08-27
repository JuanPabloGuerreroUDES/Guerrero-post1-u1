package com.patrones.u1;

// DiscountStrategy — interfaz abierta a extensión (OCP)
public interface DiscountStrategy {
    double apply(double total);
}
