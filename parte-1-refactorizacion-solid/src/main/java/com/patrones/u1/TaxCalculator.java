package com.patrones.u1;

import java.util.List;

// TaxCalculator — solo calcula impuestos (SRP)
public class TaxCalculator {

    private final double taxRate;

    public TaxCalculator(double taxRate) {
        this.taxRate = taxRate;
    }

    public double calculateTotal(List<Double> prices) {
        double subtotal = prices.stream().mapToDouble(Double::doubleValue).sum();
        return subtotal + (subtotal * taxRate);
    }
}
