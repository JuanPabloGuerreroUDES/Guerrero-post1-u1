package com.patrones.u1;

// VipDiscount — descuento del 15% para clientes VIP
public class VipDiscount implements DiscountStrategy {
    public double apply(double total) {
        return total * 0.85;
    }
}
