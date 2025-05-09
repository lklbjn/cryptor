package com.cryptor.service;

import com.cryptor.models.CoinData;
import com.cryptor.services.FavoriteCoinsService;
import com.cryptor.settings.CryptorSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CoinMarketCapService {
    private static final String BASE_URL = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest";
    private final OkHttpClient client;
    private final Gson gson;

    public CoinMarketCapService() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // 配置代理
        CryptorSettings settings = CryptorSettings.getInstance();
        if (settings != null && !settings.getProxyHost().isEmpty() && settings.getProxyPort() > 0) {
            Proxy.Type proxyType = switch (settings.getProxyType().toUpperCase()) {
                case "DIRECT" -> Proxy.Type.DIRECT;
                case "SOCKS5" -> Proxy.Type.SOCKS;
                default -> Proxy.Type.HTTP;
            };
            if (Proxy.Type.DIRECT != proxyType) {
                Proxy proxy = new Proxy(proxyType, new InetSocketAddress(settings.getProxyHost(), settings.getProxyPort()));
                clientBuilder.proxy(proxy);

                // 如果启用了代理认证
                if (settings.isProxyAuthEnabled() && !settings.getProxyUsername().isEmpty()) {
                    String credentials = okhttp3.Credentials.basic(settings.getProxyUsername(), settings.getProxyPassword());
                    clientBuilder.proxyAuthenticator((route, response) -> {
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credentials)
                                .build();
                    });
                }
            }
        }

        client = clientBuilder.build();
        gson = new Gson();
    }

    public List<CoinPriceData> getFavoriteCoinsPrices() {
        CryptorSettings settings = CryptorSettings.getInstance();
        if (settings == null) {
            return new ArrayList<>();
        }

        String apiKey = settings.getApiKey();
        if (apiKey.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有收藏的币种ID
        List<CoinData> favoriteCoins = FavoriteCoinsService.getInstance().getFavoriteCoins();
        if (favoriteCoins.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建ID查询字符串
        StringBuilder idString = new StringBuilder();
        for (CoinData coin : favoriteCoins) {
            if (!idString.isEmpty()) {
                idString.append(",");
            }
            idString.append(coin.getId());
        }

        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "?id=" + idString)
                    .addHeader("X-CMC_PRO_API_KEY", apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new ArrayList<>();
                }

                String responseBody = response.body().string();
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonObject data = jsonResponse.getAsJsonObject("data");

                List<CoinPriceData> prices = new ArrayList<>();
                for (CoinData coin : favoriteCoins) {
                    JsonObject coinData = data.getAsJsonObject(String.valueOf(coin.getId()));
                    if (coinData != null) {
                        JsonObject quote = coinData.getAsJsonObject("quote").getAsJsonObject("USD");
                        prices.add(new CoinPriceData(
                                coin.getId(),
                                coin.getName(),
                                coin.getSymbol(),
                                quote.get("price").getAsDouble(),
                                quote.get("percent_change_24h").getAsDouble()
                        ));
                    }
                }
                return prices;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static class CoinPriceData {
        private final int id;
        private final String name;
        private final String symbol;
        private final double price;
        private final double percentChange24h;

        public CoinPriceData(int id, String name, String symbol, double price, double percentChange24h) {
            this.id = id;
            this.name = name;
            this.symbol = symbol;
            this.price = price;
            this.percentChange24h = percentChange24h;
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

        public double getPrice() {
            return price;
        }

        public double getPercentChange24h() {
            return percentChange24h;
        }
    }
} 