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
    //DIRECT HTTP, HTTPS, SOCKS5
    private String proxyType = "DIRECT";
    private String proxyHost = "";
    private int proxyPort = 0;
    private boolean proxyAuthEnabled = false;
    private String proxyUsername = "";
    private String proxyPassword = "";
    private boolean redForUp = true;
    private int refreshInterval = 1;
    // SECOND, MINUTE, HOUR
    private String refreshUnit = "HOUR";
    // USD, AED, AFN, ALL, AMD, ANG, AOA, ARS, AUD, AWG, AZN, BAM, BBD, BDT, BGN, BHD, BIF, BMD, BND, BOB,
    // BRL, BSD, BTN, BWP, BYN, BZD, CAD, CDF, CHF, CLP, CNY, COP, CRC, CUP, CVE, CZK, DJF, DKK, DOP, DZD,
    // EGP, ERN, ETB, EUR, FJD, FKP, FOK, GBP, GEL, GGP, GHS, GIP, GMD, GNF, GTQ, GYD, HKD, HNL, HRK, HTG,
    // HUF, IDR, ILS, IMP, INR, IQD, IRR, ISK, JEP, JMD, JOD, JPY, KES, KGS, KHR, KID, KMF, KRW, KWD, KYD,
    // KZT, LAK, LBP, LKR, LRD, LSL, LYD, MAD, MDL, MGA, MKD, MMK, MNT, MOP, MRU, MUR, MVR, MWK, MXN, MYR,
    // MZN, NAD, NGN, NIO, NOK, NPR, NZD, OMR, PAB, PEN, PGK, PHP, PKR, PLN, PYG, QAR, RON, RSD, RUB, RWF,
    // SAR, SBD, SCR, SDG, SEK, SGD, SHP, SLE, SLL, SOS, SRD, SSP, STN, SYP, SZL, THB, TJS, TMT, TND, TOP,
    // TRY, TTD, TVD, TWD, TZS, UAH, UGX, UYU, UZS, VES, VND, VUV, WST, XAF, XCD, XCG, XDR, XOF, XPF, YER,
    // ZAR, ZMW, ZWL
    private String customPrice = "CNY";

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

    public String getCustomPrice() {
        return customPrice;
    }

    public void setCustomPrice(String customPrice) {
        this.customPrice = customPrice;
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