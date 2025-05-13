package io.github.lklbjn.cryptor.window;

import io.github.lklbjn.cryptor.model.CoinData;
import io.github.lklbjn.cryptor.model.CoinPriceData;
import io.github.lklbjn.cryptor.service.CoinMarketCapService;
import io.github.lklbjn.cryptor.services.FavoriteCoinsService;
import io.github.lklbjn.cryptor.settings.CryptorSettings;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
        priceTable.getColumnModel().getColumn(0).setPreferredWidth(130); // Name
        priceTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Symbol
        priceTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Price(USD)
        priceTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Price(Custom)
        priceTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 24h Change
        priceTable.getColumnModel().getColumn(5).setPreferredWidth(100); // 7d Change

        // 设置24h Change和7d Change自定义渲染器
        priceTable.getColumnModel().getColumn(4).setCellRenderer(new ChangeColorRenderer(settings));
        priceTable.getColumnModel().getColumn(5).setCellRenderer(new ChangeColorRenderer(settings));

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
            String priceFormat = "$%." + settings.getPriceDecimalPlaces() + "f";
            String customPriceFormat = "¤%." + settings.getPriceDecimalPlaces() + "f";
            String changeFormat = "%." + settings.getChangeDecimalPlaces() + "f%%";
            
            String priceText = String.format(priceFormat, price.getPrice());
            String customPriceText = String.format(customPriceFormat, price.getCustomPrice());
            String change24hText = String.format(changeFormat, price.getPercentChange24h());
            String change7dText = String.format(changeFormat, price.getPercentChange7d());

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
        if (settings.getApiKey() == null || settings.getApiKey().isEmpty()) {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Cryptor.Notifications")
                    .createNotification("Please Set ApiKey First", NotificationType.WARNING)
                    .notify(project);
            return;
        }
        if (refreshTimer != null) {
            return;
        }
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshData());
            }
        }, 0, settings.getRefreshIntervalInMillis());

        NotificationGroupManager.getInstance()
                .getNotificationGroup("Cryptor.Notifications")
                .createNotification("StartAutoRefresh Successfully: " + LocalDateTime.now(), NotificationType.INFORMATION)
                .notify(project);
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Cryptor.Notifications")
                .createNotification("StopAutoRefresh Successfully: " + LocalDateTime.now(), NotificationType.INFORMATION)
                .notify(project);
    }

    /**
     * 涨跌幅颜色渲染器
     */
    private static class ChangeColorRenderer extends DefaultTableCellRenderer {
        private final CryptorSettings settings;

        public ChangeColorRenderer(CryptorSettings settings) {
            this.settings = settings;
        }

        private static final JBColor RED = new JBColor(new Color(255, 59, 48), new Color(255, 99, 88));
        private static final JBColor GREEN = new JBColor(new Color(50, 205, 50), new Color(100, 255, 100));

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = value != null ? value.toString().replace("%", "") : "0";
            double val = 0;
            try {
                val = Double.parseDouble(text);
            } catch (Exception ignored) {
                val = 0;
            }
            boolean redForUp = settings.isRedForUp();
            if (val > 0) {
                c.setForeground(redForUp ? RED : GREEN); // 红或绿
            } else if (val < 0) {
                c.setForeground(redForUp ? GREEN : RED); // 绿或红
            } else {
                c.setForeground(JBColor.GRAY);
            }
            return c;
        }
    }
}