package io.github.lklbjn.cryptor.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CryptorConfigurable implements Configurable {
    private CryptorSettingsComponent settingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Cryptor Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingsComponent = new CryptorSettingsComponent();
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        CryptorSettings settings = CryptorSettings.getInstance();
        return !settingsComponent.getApiKey().equals(settings.getApiKey()) ||
                !settingsComponent.getProxyType().equals(settings.getProxyType()) ||
                !settingsComponent.getProxyHost().equals(settings.getProxyHost()) ||
                settingsComponent.getProxyPort() != settings.getProxyPort() ||
                settingsComponent.isProxyAuthEnabled() != settings.isProxyAuthEnabled() ||
                !settingsComponent.getProxyUsername().equals(settings.getProxyUsername()) ||
                !settingsComponent.getProxyPassword().equals(settings.getProxyPassword()) ||
                settingsComponent.isRedForUp() != settings.isRedForUp() ||
                settingsComponent.getRefreshInterval() != settings.getRefreshInterval() ||
                !settingsComponent.getRefreshUnit().equals(settings.getRefreshUnit()) ||
                !settingsComponent.getCustomPrice().equals(settings.getCustomPrice()) ||
                settingsComponent.getPriceDecimalPlaces() != settings.getPriceDecimalPlaces() ||
                settingsComponent.getChangeDecimalPlaces() != settings.getChangeDecimalPlaces();
    }

    @Override
    public void apply() {
        CryptorSettings settings = CryptorSettings.getInstance();
        settings.setApiKey(settingsComponent.getApiKey());
        settings.setProxyType(settingsComponent.getProxyType());
        settings.setProxyHost(settingsComponent.getProxyHost());
        settings.setProxyPort(settingsComponent.getProxyPort());
        settings.setProxyAuthEnabled(settingsComponent.isProxyAuthEnabled());
        settings.setProxyUsername(settingsComponent.getProxyUsername());
        settings.setProxyPassword(settingsComponent.getProxyPassword());
        settings.setRedForUp(settingsComponent.isRedForUp());
        settings.setRefreshInterval(settingsComponent.getRefreshInterval());
        settings.setRefreshUnit(settingsComponent.getRefreshUnit());
        settings.setCustomPrice(settingsComponent.getCustomPrice());
        settings.setPriceDecimalPlaces(settingsComponent.getPriceDecimalPlaces());
        settings.setChangeDecimalPlaces(settingsComponent.getChangeDecimalPlaces());
    }

    @Override
    public void reset() {
        CryptorSettings settings = CryptorSettings.getInstance();
        settingsComponent.setApiKey(settings.getApiKey());
        settingsComponent.setProxyType(settings.getProxyType());
        settingsComponent.setProxyHost(settings.getProxyHost());
        settingsComponent.setProxyPort(settings.getProxyPort());
        settingsComponent.setProxyAuthEnabled(settings.isProxyAuthEnabled());
        settingsComponent.setProxyUsername(settings.getProxyUsername());
        settingsComponent.setProxyPassword(settings.getProxyPassword());
        settingsComponent.setRedForUp(settings.isRedForUp());
        settingsComponent.setRefreshInterval(settings.getRefreshInterval());
        settingsComponent.setRefreshUnit(settings.getRefreshUnit());
        settingsComponent.setCustomPrice(settings.getCustomPrice());
        settingsComponent.setPriceDecimalPlaces(settings.getPriceDecimalPlaces());
        settingsComponent.setChangeDecimalPlaces(settings.getChangeDecimalPlaces());
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
} 