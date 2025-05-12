package com.cryptor.actions;

import com.cryptor.model.CoinData;
import com.cryptor.services.FavoriteCoinsService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageFavoriteCoinsAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        ManageFavoriteCoinsDialog dialog = new ManageFavoriteCoinsDialog();
        dialog.show();
    }

    private static class ManageFavoriteCoinsDialog extends DialogWrapper {
        private final JTable favoriteTable;
        private final DefaultTableModel tableModel;
        private final FavoriteCoinsService favoriteCoinsService;

        public ManageFavoriteCoinsDialog() {
            super(true);
            setTitle("Manage Favorite Coins");

            favoriteCoinsService = FavoriteCoinsService.getInstance();

            String[] columnNames = {"ID", "Name", "Symbol", "Slug", "Action"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4; // 只有Action列可编辑
                }
            };
            favoriteTable = new JTable(tableModel);

            // 设置表格列宽
            favoriteTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
            favoriteTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
            favoriteTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Symbol
            favoriteTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Slug
            favoriteTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Action

            // 添加表格按钮渲染器
            favoriteTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
            favoriteTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

            // 加载收藏的币种
            loadFavoriteCoins();

            init();
        }

        private void loadFavoriteCoins() {
            tableModel.setRowCount(0);
            for (CoinData coin : favoriteCoinsService.getFavoriteCoins()) {
                tableModel.addRow(new Object[]{
                    coin.getId(),
                    coin.getName(),
                    coin.getSymbol(),
                    coin.getSlug(),
                    "Remove"
                });
            }
        }

        @Override
        protected JComponent createCenterPanel() {
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.add(new JScrollPane(favoriteTable), BorderLayout.CENTER);

            return FormBuilder.createFormBuilder()
                    .addComponent(tablePanel)
                    .addComponentFillVertically(new JPanel(), 0)
                    .getPanel();
        }

        // 按钮渲染器
        private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
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
                    CoinData coin = favoriteCoinsService.getFavoriteCoins().get(row);
                    favoriteCoinsService.removeFavoriteCoin(coin);
                    Messages.showInfoMessage("Removed " + coin.getName() + " from favorites", "Favorite Coins");
                    loadFavoriteCoins(); // 重新加载表格
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