package com.cryptor.actions;

import com.cryptor.model.CoinData;
import com.cryptor.services.FavoriteCoinsService;
import com.cryptor.settings.CryptorSettings;
import com.cryptor.window.CryptorToolWindow;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("KotlinInternalInJava")
public class AddFavoriteCoinAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        AddCoinDialog dialog = new AddCoinDialog(e.getProject());
        dialog.show();
    }

    private static class AddCoinDialog extends DialogWrapper {
        private final JBTextField searchField;
        private final JTable resultTable;
        private final DefaultTableModel tableModel;
        private final OkHttpClient client;
        private final List<CoinData> searchResults;
        private final FavoriteCoinsService favoriteCoinsService;
        private final Project project;

        public AddCoinDialog(Project project) {
            super(true);
            setTitle("Add Favorite Coin");
            this.project = project;

            favoriteCoinsService = FavoriteCoinsService.getInstance();

            // 配置OkHttpClient
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
                        clientBuilder.proxyAuthenticator((route, response) -> response.request().newBuilder()
                                .header("Proxy-Authorization", credentials)
                                .build());
                    }
                }
            }

            client = clientBuilder.build();
            searchResults = new ArrayList<>();

            searchField = new JBTextField();
            String[] columnNames = {"ID", "Name", "Symbol", "Slug", "Action"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4; // 只有Action列可编辑
                }
            };

            resultTable = new JTable(tableModel);

            // 设置表格列宽
            resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
            resultTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
            resultTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Symbol
            resultTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Slug
            resultTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Action

            // 添加表格按钮渲染器
            resultTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
            resultTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

            init();
        }

        private void searchCoins(String query) {
            CryptorSettings settings = CryptorSettings.getInstance();
            if (settings == null) {
                return;
            }

            String apiKey = settings.getApiKey();
            if (apiKey.isEmpty() || query.isEmpty()) {
                return;
            }

            try {
                Request request = new Request.Builder()
                        .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/map?symbol=" + query)
                        .addHeader("X-CMC_PRO_API_KEY", apiKey)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray data = jsonResponse.getAsJsonArray("data");

                    SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);
                        searchResults.clear();
                        for (int i = 0; i < data.size(); i++) {
                            JsonObject coin = data.get(i).getAsJsonObject();
                            CoinData coinData = new CoinData(
                                coin.get("id").getAsInt(),
                                coin.get("name").getAsString(),
                                coin.get("symbol").getAsString(),
                                coin.get("slug").getAsString()
                            );
                            searchResults.add(coinData);
                            String buttonText = favoriteCoinsService.isFavorite(coinData) ? "Remove" : "Add";
                            tableModel.addRow(new Object[]{
                                coinData.getId(),
                                coinData.getName(),
                                coinData.getSymbol(),
                                coinData.getSlug(),
                                buttonText
                            });
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(new JBLabel("Search: "), BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(e -> searchCoins(searchField.getText()));
            searchPanel.add(searchButton, BorderLayout.EAST);
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
            return FormBuilder.createFormBuilder()
                    .addComponent(searchPanel)
                    .addComponent(tablePanel)
                    .getPanel();
        }

        @Override
        protected void doOKAction() {
            super.doOKAction();
            // 关闭对话框后自动刷新价格显示
            if (project != null) {
                CryptorToolWindow toolWindow = project.getService(CryptorToolWindow.class);
                if (toolWindow != null) {
                    toolWindow.refreshData();
                }
            }
        }

        // 按钮渲染器
        private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
            public ButtonRenderer() {
                setOpaque(true);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                setText((value == null) ? "" : value.toString());
                return this;
            }
        }

        // 按钮编辑器
        private class ButtonEditor extends DefaultCellEditor {
            protected JButton button;
            private String label;
            private boolean isPushed;
            private int row;

            public ButtonEditor(JCheckBox checkBox) {
                super(checkBox);
                button = new JButton();
                button.setOpaque(true);
                button.addActionListener(e -> fireEditingStopped());
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                label = (value == null) ? "" : value.toString();
                button.setText(label);
                isPushed = true;
                this.row = row;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                if (isPushed) {
                    CoinData selectedCoin = searchResults.get(row);
                    if (favoriteCoinsService.isFavorite(selectedCoin)) {
                        favoriteCoinsService.removeFavoriteCoin(selectedCoin);
                        Messages.showInfoMessage("Removed " + selectedCoin.getName() + " from favorites", "Favorite Coins");
                        SwingUtilities.invokeLater(() -> {
                            tableModel.setValueAt("Add", row, 4);
                            resultTable.repaint();
                        });
                    } else {
                        favoriteCoinsService.addFavoriteCoin(selectedCoin);
                        Messages.showInfoMessage("Added " + selectedCoin.getName() + " to favorites", "Favorite Coins");
                        SwingUtilities.invokeLater(() -> {
                            tableModel.setValueAt("Remove", row, 4);
                            resultTable.repaint();
                        });
                    }
                }
                isPushed = false;
                return label;
            }

            @Override
            public boolean stopCellEditing() {
                isPushed = false;
                return super.stopCellEditing();
            }
        }
    }
} 