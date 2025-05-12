package com.cryptor.services;

import com.cryptor.model.CoinData;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "FavoriteCoinsService",
        storages = @Storage("FavoriteCoins.xml")
)
public class FavoriteCoinsService implements PersistentStateComponent<FavoriteCoinsService> {
    private final List<CoinData> favoriteCoins = new ArrayList<>();

    public static FavoriteCoinsService getInstance() {
        return ApplicationManager.getApplication().getService(FavoriteCoinsService.class);
    }

    public List<CoinData> getFavoriteCoins() {
        return favoriteCoins;
    }

    public void addFavoriteCoin(CoinData coin) {
        if (!favoriteCoins.contains(coin)) {
            favoriteCoins.add(coin);
        }
    }

    public void removeFavoriteCoin(CoinData coin) {
        favoriteCoins.remove(coin);
    }

    public boolean isFavorite(CoinData coin) {
        return favoriteCoins.contains(coin);
    }

    @Nullable
    @Override
    public FavoriteCoinsService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull FavoriteCoinsService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
} 