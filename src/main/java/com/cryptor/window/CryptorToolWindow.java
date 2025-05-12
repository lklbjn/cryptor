package com.cryptor.window;

import com.cryptor.model.CoinData;
import com.cryptor.model.CoinPriceData;
import com.cryptor.service.CoinMarketCapService;
import com.cryptor.services.FavoriteCoinsService;
import com.cryptor.settings.CryptorSettings;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service(Service.Level.PROJECT)
public final class CryptorToolWindow {
    private final Project project;
    private final JPanel mainPanel;
    private final JBTable priceTable;
    private final DefaultTableModel tableModel;
    private final CoinMarketCapService coinMarketCapService;
    private Timer refreshTimer;
    private final CryptorSettings settings;
    private final FavoriteCoinsService favoriteCoinsService;

    public CryptorToolWindow(Project project) {
        this.project = project;
        this.settings = CryptorSettings.getInstance();
        this.coinMarketCapService = new CoinMarketCapService();
        this.favoriteCoinsService = FavoriteCoinsService.getInstance();

        // 创建工具栏
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(
                "Cryptor.Toolbar",
                (ActionGroup) ActionManager.getInstance().getAction("Cryptor.Toolbar"),
                true
        );
        String customPrice = settings.getCustomPrice();
        // 创建表格
        String[] columnNames = {"Name", "Symbol", "Price (USD)", "Price (" + customPrice + ")", "24h Change", "7d Change"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        priceTable = new JBTable(tableModel);
        priceTable.setStriped(true);
        priceTable.setShowGrid(false);
        priceTable.setIntercellSpacing(JBUI.emptySize());

        // 设置列宽
        priceTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Name
        priceTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Symbol
        priceTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Price(USD)
        priceTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Price(Custom)
        priceTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 24h Change
        priceTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 7d Change

        // 创建主面板
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolbar.getComponent(), BorderLayout.NORTH);
        mainPanel.add(new JBScrollPane(priceTable), BorderLayout.CENTER);

        // 初始加载数据
        startAutoRefresh();
    }

    public JComponent getContent() {
        return mainPanel;
    }

    public void refreshData() {
        if (settings == null) {
            return;
        }
        String customPrice = settings.getCustomPrice();
        if (!tableModel.getColumnName(3).equals("Price (" + customPrice + ")")) {
            // 通知表格模型数据已更改
            updateCustomPriceColumn(customPrice);
        }
        List<CoinData> favoriteCoins = favoriteCoinsService.getFavoriteCoins();
        if (favoriteCoins.isEmpty()) {
            tableModel.setRowCount(0);
            return;
        }
        List<CoinPriceData> prices = coinMarketCapService.getFavoriteCoinsPrices();
        // 清空表格
        tableModel.setRowCount(0);
        // 添加新数据
        for (CoinPriceData price : prices) {
            String priceText = String.format("$%.2f", price.getPrice());
            String customPriceText = String.format("%.2f", price.getCustomPrice());
            String change24hText = String.format("%.2f%%", price.getPercentChange24h());
            String change7dText = String.format("%.2f%%", price.getPercentChange7d());

            tableModel.addRow(new Object[]{
                    price.getName(),
                    price.getSymbol(),
                    priceText,
                    customPriceText,
                    change24hText,
                    change7dText
            });
        }

        NotificationGroupManager.getInstance()
                .getNotificationGroup("Cryptor.Notifications")
                .createNotification("RefreshData Successfully: " + LocalDateTime.now(), NotificationType.INFORMATION)
                .notify(project);
    }

    private void updateCustomPriceColumn(String newCustomPrice) {
        if (settings == null) {
            return;
        }

        // 1. 保存当前表格数据
        Object[][] currentData = new Object[tableModel.getRowCount()][tableModel.getColumnCount()];
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                currentData[row][col] = tableModel.getValueAt(row, col);
            }
        }

        // 2. 创建新的列名数组
        String[] columnNames = {"Name", "Symbol", "Price (USD)", "Price (" + newCustomPrice + ")", "24h Change", "7d Change"};

        // 3. 清空表格
        tableModel.setRowCount(0);

        // 4. 设置新的列名
        for (int i = 0; i < columnNames.length; i++) {
            tableModel.setColumnIdentifiers(columnNames);
        }

        // 5. 恢复表格数据
        for (Object[] rowData : currentData) {
            tableModel.addRow(rowData);
        }

        // 6. 通知表格模型数据已更改
        tableModel.fireTableStructureChanged();

        NotificationGroupManager.getInstance()
                .getNotificationGroup("Cryptor.Notifications")
                .createNotification("CustomPrice Changed: " + LocalDateTime.now(), NotificationType.INFORMATION)
                .notify(project);
    }

    public void startAutoRefresh() {
        if (settings == null) {
            return;
        }

        stopAutoRefresh();
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshData());
            }
        }, 0, settings.getRefreshIntervalInMillis());
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }
}