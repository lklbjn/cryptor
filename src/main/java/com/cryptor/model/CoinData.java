package com.cryptor.model;

public class CoinData {
    private String symbol;
    private String name;
    private double usdPrice;
    private double cnyPrice;
    private double change24h;
    private double change7d;

    public CoinData(String symbol, String name, double usdPrice, double cnyPrice, double change24h, double change7d) {
        this.symbol = symbol;
        this.name = name;
        this.usdPrice = usdPrice;
        this.cnyPrice = cnyPrice;
        this.change24h = change24h;
        this.change7d = change7d;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getUsdPrice() {
        return usdPrice;
    }

    public double getCnyPrice() {
        return cnyPrice;
    }

    public double getChange24h() {
        return change24h;
    }

    public double getChange7d() {
        return change7d;
    }
} 