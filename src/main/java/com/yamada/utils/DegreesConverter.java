package com.yamada.utils;

public class DegreesConverter {

    public static final double KELVIN_CONSTANT = 273.15;
    private static DegreesConverter instance;
    private DegreesConverter() { }

    public static DegreesConverter getInstance() {
        if (instance == null) {
            instance = new DegreesConverter();
        }

        return instance;
    }


    public double celsiusToFahrenheit(double value) {
//        System.out.println(value * 9 / 5 + 32);
        return value * 9 / 5 + 32;
    }

    public double celsiusToKelvin(double value) {
        return value + KELVIN_CONSTANT;
    }

    public double fahrenheitToCelsius(double value) {
//        System.out.println((value - 32) * 5 / 9);
        return (value - 32) * 5 / 9;
    }

    public double fahrenheitToKelvin(double value) {
        return fahrenheitToCelsius(value) + KELVIN_CONSTANT;
    }

    public double kelvinToCelsius(double value) {
        return value - KELVIN_CONSTANT;
    }

    public double kelvinToFahrenheit(double value) {
        return celsiusToFahrenheit(value - KELVIN_CONSTANT);
    }


}
