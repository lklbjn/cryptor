package io.github.lklbjn.cryptor.model;

public class CoinPriceData {
    private final Integer id;
    private final String name;
    private final String symbol;
    private final Double price;
    private final Double customPrice;
    private final Double percentChange24h;
    private final Double percentChange7d;

    public CoinPriceData(Integer id, String name, String symbol, Double price, Double customPrice, Double percentChange24h, Double percentChange7d) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.customPrice = customPrice;
        this.percentChange24h = percentChange24h;
        this.percentChange7d = percentChange7d;
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

    public Double getPrice() {
        return price;
    }

    public Double getCustomPrice() {
        return customPrice;
    }

    public Double getPercentChange24h() {
        return percentChange24h;
    }

    public Double getPercentChange7d() {
        return percentChange7d;
    }
}