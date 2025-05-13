package io.github.lklbjn.cryptor.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoinData coinData = (CoinData) o;
        return Objects.equals(id, coinData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}