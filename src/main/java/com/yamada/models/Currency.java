package com.yamada.models;

public record Currency(String currencyCode, String currencyName) {

    @Override
    public String toString() {
        return this.currencyCode+" - "+this.currencyName;
    }
}
