package com.yamada.models;

public enum Degree {

    CELSIUS("C","Celsius"), KELVIN("K","Kelvin"), FAHRENHEIT("F","Fahrenheit");

    private final String value;
    private final String name;

    Degree(final String value, final String name) {
        this.value = value;
        this.name = name;
    }


    @Override
    public String toString() {
        return this.value+" - "+this.name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
