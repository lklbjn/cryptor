package com.cryptor.models;

import java.util.Objects;

public class CoinData {
    private final int id;
    private final String name;
    private final String symbol;
    private final String slug;

    public CoinData(int id, String name, String symbol, String slug) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinData coinData = (CoinData) o;
        return id == coinData.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
} 