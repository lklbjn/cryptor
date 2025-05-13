package io.github.lklbjn.cryptor.services;

import io.github.lklbjn.cryptor.model.CoinData;
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
        return new ArrayList<>(favoriteCoins);
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

    public void moveCoin(int fromIndex, int toIndex) {
        if (fromIndex >= 0 && fromIndex < favoriteCoins.size() && 
            toIndex >= 0 && toIndex < favoriteCoins.size()) {
            CoinData coin = favoriteCoins.remove(fromIndex);
            favoriteCoins.add(toIndex, coin);
        }
    }

    public void reorderCoins(List<CoinData> newOrder) {
        favoriteCoins.clear();
        favoriteCoins.addAll(newOrder);
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