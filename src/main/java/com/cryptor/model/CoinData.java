package com.cryptor.model;

public class CoinData {
    private final Integer id;
    private final String name;
    private final String symbol;
    private final String slug;

    public CoinData(Integer id, String name, String symbol, String slug) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.slug = slug;
    }

    public Integer getId() {
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
}