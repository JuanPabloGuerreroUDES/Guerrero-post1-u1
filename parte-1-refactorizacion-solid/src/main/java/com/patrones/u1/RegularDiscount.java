package com.patrones.u1;

// RegularDiscount — descuento del 5% para clientes regulares
public class RegularDiscount implements DiscountStrategy {
    public double apply(double total) {
        return total * 0.95;
    }
}
