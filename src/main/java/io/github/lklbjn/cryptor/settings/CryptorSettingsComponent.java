package io.github.lklbjn.cryptor.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("KotlinInternalInJava")
public class CryptorSettingsComponent {
    private final JPanel mainPanel;
    private final JBTextField apiKeyField = new JBTextField();
    private final ComboBox<String> proxyTypeCombo = new ComboBox<>(new String[]{"DIRECT", "HTTP", "HTTPS", "SOCKS5"});
    private final JBTextField proxyHostField = new JBTextField();
    private final JBTextField proxyPortField = new JBTextField();
    private final JBCheckBox proxyAuthCheckBox = new JBCheckBox("Enable Proxy Authentication");
    private final JBTextField proxyUsernameField = new JBTextField();
    private final JBTextField proxyPasswordField = new JBTextField();
    private final JRadioButton redForUpRadio = new JRadioButton("Red for Up");
    private final JRadioButton greenForUpRadio = new JRadioButton("Green for Up");
    private final ButtonGroup colorSchemeGroup = new ButtonGroup();
    private final JBTextField refreshIntervalField = new JBTextField();
    private final ComboBox<String> refreshUnitComboBox = new ComboBox<>(new String[]{"SECOND", "MINUTE", "HOUR"});
    private final ComboBox<String> customPriceComboBox = new ComboBox<>(new String[]{"USD", "AED", "AFN", "ALL", "AMD",
            "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB",
            "BRL", "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE",
            "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD", "FKP", "FOK", "GBP", "GEL", "GGP",
            "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR",
            "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KID", "KMF", "KRW", "KWD", "KYD",
            "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU",
            "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN",
            "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK",
            "SGD", "SHP", "SLE", "SLL", "SOS", "SRD", "SSP", "STN", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP",
            "TRY", "TTD", "TVD", "TWD", "TZS", "UAH", "UGX", "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF", "XCD",
            "XCG", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"});
    private final JBTextField priceDecimalPlacesField = new JBTextField();
    private final JBTextField changeDecimalPlacesField = new JBTextField();

    public CryptorSettingsComponent() {
        // 创建代理认证面板
        JPanel proxyAuthPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Username: "), proxyUsernameField)
                .addLabeledComponent(new JBLabel("Password: "), proxyPasswordField)
                .getPanel();

        // 创建测试按钮
        JButton testButton = new JButton("Test Proxy");
        testButton.addActionListener(e -> testProxy());

        // 创建代理设置面板
        JPanel proxyPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Proxy Type: "), proxyTypeCombo)
                .addLabeledComponent(new JBLabel("Host: "), proxyHostField)
                .addLabeledComponent(new JBLabel("Port: "), proxyPortField)
                .addComponent(proxyAuthCheckBox)
                .addComponent(proxyAuthPanel)
                .addComponent(testButton)
                .getPanel();

        // 设置代理认证面板的可见性
        proxyAuthPanel.setVisible(false);
        proxyAuthCheckBox.addActionListener(e -> proxyAuthPanel.setVisible(proxyAuthCheckBox.isSelected()));

        // 设置刷新间隔输入框只允许输入整数
        refreshIntervalField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = ((JTextField) input).getText();
                try {
                    int value = Integer.parseInt(text);
                    return value > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });

        // 设置小数位数输入框只允许输入0-10的整数
        InputVerifier decimalPlacesVerifier = new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = ((JTextField) input).getText();
                try {
                    int value = Integer.parseInt(text);
                    return value >= 0 && value <= 10;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        };
        priceDecimalPlacesField.setInputVerifier(decimalPlacesVerifier);
        changeDecimalPlacesField.setInputVerifier(decimalPlacesVerifier);

        // 设置颜色方案单选按钮组
        colorSchemeGroup.add(redForUpRadio);
        colorSchemeGroup.add(greenForUpRadio);
        redForUpRadio.setSelected(true);

        // 创建颜色方案面板
        JPanel colorSchemePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorSchemePanel.add(redForUpRadio);
        colorSchemePanel.add(greenForUpRadio);

        // 创建主面板
        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("API Key: "), apiKeyField)
                .addSeparator()
                .addComponent(new JBLabel("Proxy Settings"))
                .addComponent(proxyPanel)
                .addSeparator()
                .addComponent(new JBLabel("Color Scheme: "))
                .addComponent(colorSchemePanel)
                .addSeparator()
                .addLabeledComponent(new JBLabel("Refresh Interval: "), refreshIntervalField)
                .addLabeledComponent(new JBLabel("Refresh Unit: "), refreshUnitComboBox)
                .addLabeledComponent(new JBLabel("Custom Price: "), customPriceComboBox)
                .addLabeledComponent(new JBLabel("Price Decimal Places (0-10): "), priceDecimalPlacesField)
                .addLabeledComponent(new JBLabel("Change Decimal Places (0-10): "), changeDecimalPlacesField)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private void testProxy() {
        String host = proxyHostField.getText();
        String portText = proxyPortField.getText();

        if (host.isEmpty() || portText.isEmpty()) {
            Messages.showErrorDialog("Please enter proxy host and port", "Proxy Test Failed");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            Messages.showErrorDialog("Invalid port number", "Proxy Test Failed");
            return;
        }

        // 配置OkHttpClient
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);

        // 配置代理
        String proxyTypeStr = Objects.requireNonNull(proxyTypeCombo.getSelectedItem()).toString();
        Proxy.Type proxyType = switch (proxyTypeStr.toUpperCase()) {
            case "DIRECT" -> Proxy.Type.DIRECT;
            case "SOCKS5" -> Proxy.Type.SOCKS;
            default -> Proxy.Type.HTTP;
        };

        if (Proxy.Type.DIRECT != proxyType) {
            Proxy proxy = new Proxy(proxyType, new InetSocketAddress(host, port));
            clientBuilder.proxy(proxy);
            // 如果启用了代理认证
            if (proxyAuthCheckBox.isSelected() && !proxyUsernameField.getText().isEmpty()) {
                String credentials = okhttp3.Credentials.basic(
                        proxyUsernameField.getText(),
                        proxyPasswordField.getText()
                );
                clientBuilder.proxyAuthenticator((route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", credentials)
                        .build());
            }
        }

        OkHttpClient client = clientBuilder.build();

        // 测试连接
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/map?symbol=BTC")
                        .addHeader("X-CMC_PRO_API_KEY", apiKeyField.getText())
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        SwingUtilities.invokeLater(() ->
                                Messages.showInfoMessage("Proxy connection successful!", "Proxy Test"));
                    } else {
                        SwingUtilities.invokeLater(() ->
                                Messages.showErrorDialog("Failed to connect through proxy: " + response.code(), "Proxy Test Failed"));
                    }
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        Messages.showErrorDialog("Failed to connect through proxy: " + e.getMessage(), "Proxy Test Failed"));
            }
        }).start();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return apiKeyField;
    }

    public String getApiKey() {
        return apiKeyField.getText();
    }

    public void setApiKey(String apiKey) {
        apiKeyField.setText(apiKey);
    }

    public String getProxyType() {
        return (String) proxyTypeCombo.getSelectedItem();
    }

    public void setProxyType(String proxyType) {
        proxyTypeCombo.setSelectedItem(proxyType);
    }

    public String getProxyHost() {
        return proxyHostField.getText();
    }

    public void setProxyHost(String host) {
        proxyHostField.setText(host);
    }

    public int getProxyPort() {
        try {
            return Integer.parseInt(proxyPortField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setProxyPort(int port) {
        proxyPortField.setText(String.valueOf(port));
    }

    public boolean isProxyAuthEnabled() {
        return proxyAuthCheckBox.isSelected();
    }

    public void setProxyAuthEnabled(boolean enabled) {
        proxyAuthCheckBox.setSelected(enabled);
        proxyAuthCheckBox.getActionListeners()[0].actionPerformed(null);
    }

    public String getProxyUsername() {
        return proxyUsernameField.getText();
    }

    public void setProxyUsername(String username) {
        proxyUsernameField.setText(username);
    }

    public String getProxyPassword() {
        return proxyPasswordField.getText();
    }

    public void setProxyPassword(String password) {
        proxyPasswordField.setText(password);
    }

    public boolean isRedForUp() {
        return redForUpRadio.isSelected();
    }

    public void setRedForUp(boolean redForUp) {
        if (redForUp) {
            redForUpRadio.setSelected(true);
        } else {
            greenForUpRadio.setSelected(true);
        }
    }

    public int getRefreshInterval() {
        try {
            return Integer.parseInt(refreshIntervalField.getText());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void setRefreshInterval(int interval) {
        refreshIntervalField.setText(String.valueOf(interval));
    }

    public String getRefreshUnit() {
        return (String) refreshUnitComboBox.getSelectedItem();
    }

    public void setRefreshUnit(String unit) {
        refreshUnitComboBox.setSelectedItem(unit);
    }

    public String getCustomPrice() {
        return (String) customPriceComboBox.getSelectedItem();
    }

    public void setCustomPrice(String customPrice) {
        customPriceComboBox.setSelectedItem(customPrice);
    }

    public int getPriceDecimalPlaces() {
        try {
            return Integer.parseInt(priceDecimalPlacesField.getText());
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    public void setPriceDecimalPlaces(int places) {
        priceDecimalPlacesField.setText(String.valueOf(places));
    }

    public int getChangeDecimalPlaces() {
        try {
            return Integer.parseInt(changeDecimalPlacesField.getText());
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    public void setChangeDecimalPlaces(int places) {
        changeDecimalPlacesField.setText(String.valueOf(places));
    }
}