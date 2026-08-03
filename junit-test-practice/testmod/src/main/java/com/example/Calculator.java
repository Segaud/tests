package com.example;

public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public double divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("Division by zero is not allowed.");
        return a / b;
    }

    public int clamp(int value, int min, int max) {
        if (min > max) throw new IllegalArgumentException("Minimum value cannot be greater than maximum value.");
        return Math.min(Math.max(value, min), max);
    }
}
