package io.github.lklbjn.cryptor.service;

import io.github.lklbjn.cryptor.model.CoinData;
import io.github.lklbjn.cryptor.model.CoinPriceData;
import io.github.lklbjn.cryptor.services.FavoriteCoinsService;
import io.github.lklbjn.cryptor.settings.CryptorSettings;
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
    private static final String EXCHANGE_RATE_URL = "https://open.er-api.com/v6/latest/USD";
    private final OkHttpClient client;

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

        String customPriceName = settings.getCustomPrice();
        double customPrice = 1;
        if (!customPriceName.isEmpty()) {
            Request request = new Request.Builder()
                    .url(EXCHANGE_RATE_URL)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject customPriceData = jsonResponse.getAsJsonObject("rates");
                    customPrice = customPriceData.get(customPriceName).getAsDouble();
                }
            } catch (IOException e) {
                System.out.println("Failed to fetch exchange rate data: " + e.getMessage());
            }
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
                assert response.body() != null;
                String responseBody = response.body().string();
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonObject data = jsonResponse.getAsJsonObject("data");

                List<CoinPriceData> prices = new ArrayList<>();
                for (CoinData coin : favoriteCoins) {
                    JsonObject coinData = data.getAsJsonObject(String.valueOf(coin.getId()));
                    if (coinData != null) {
                        JsonObject quote = coinData.getAsJsonObject("quote").getAsJsonObject("USD");
                        double price = quote.get("price").getAsDouble();
                        prices.add(new CoinPriceData(
                                coin.getId(),
                                coin.getName(),
                                coin.getSymbol(),
                                price,
                                price * customPrice,
                                quote.get("percent_change_24h").getAsDouble(),
                                quote.get("percent_change_7d").getAsDouble()
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


} 