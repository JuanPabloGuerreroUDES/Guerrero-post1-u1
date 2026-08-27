package com.patrones.u1;

// NoDiscount — sin descuento, retorna el total sin cambios
public class NoDiscount implements DiscountStrategy {
    public double apply(double total) {
        return total;
    }
}
