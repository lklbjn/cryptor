package com.cryptor.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author lklbjn
 */
@State(
        name = "CryptorSettings",
        storages = @Storage("cryptor.xml")
)
public class CryptorSettings implements PersistentStateComponent<CryptorSettings> {
    private String apiKey = "";
    private String proxyType = "DIRECT"; //DIRECT HTTP, HTTPS, SOCKS5
    private String proxyHost = "";
    private int proxyPort = 0;
    private boolean proxyAuthEnabled = false;
    private String proxyUsername = "";
    private String proxyPassword = "";
    private boolean redForUp = true;
    private int refreshInterval = 1;
    private String refreshUnit = "HOUR"; // SECOND, MINUTE, HOUR

    public static CryptorSettings getInstance() {
        return ApplicationManager.getApplication().getService(CryptorSettings.class);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isProxyAuthEnabled() {
        return proxyAuthEnabled;
    }

    public void setProxyAuthEnabled(boolean proxyAuthEnabled) {
        this.proxyAuthEnabled = proxyAuthEnabled;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public boolean isRedForUp() {
        return redForUp;
    }

    public void setRedForUp(boolean redForUp) {
        this.redForUp = redForUp;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getRefreshUnit() {
        return refreshUnit;
    }

    public void setRefreshUnit(String refreshUnit) {
        this.refreshUnit = refreshUnit;
    }

    public long getRefreshIntervalInMillis() {
        System.out.println("The interval time is: " + refreshInterval + " " + refreshUnit);
        return switch (refreshUnit) {
            case "SECOND" -> refreshInterval * 1000L;
            case "MINUTE" -> refreshInterval * 60000L;
            case "HOUR" -> refreshInterval * 3600000L;
            default -> 3600000L;
        };
    }

    @Override
    public @Nullable CryptorSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CryptorSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
} 