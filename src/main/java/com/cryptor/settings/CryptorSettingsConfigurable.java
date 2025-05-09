package com.cryptor.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CryptorSettingsConfigurable implements Configurable {
    private JPanel mainPanel;
    private JBTextField apiKeyField;
    private ComboBox<String> colorSchemeComboBox;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Cryptor Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        apiKeyField = new JBTextField();
        colorSchemeComboBox = new ComboBox<>(new String[]{"Red for Up", "Green for Up"});

        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("API Key: "), apiKeyField, 1, false)
                .addLabeledComponent(new JBLabel("Color Scheme: "), colorSchemeComboBox, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        CryptorSettings settings = CryptorSettings.getInstance();
        return !apiKeyField.getText().equals(settings.getApiKey()) ||
                (colorSchemeComboBox.getSelectedIndex() == 0) != settings.isRedForUp();
    }

    @Override
    public void apply() throws ConfigurationException {
        CryptorSettings settings = CryptorSettings.getInstance();
        settings.setApiKey(apiKeyField.getText());
        settings.setRedForUp(colorSchemeComboBox.getSelectedIndex() == 0);
    }

    @Override
    public void reset() {
        CryptorSettings settings = CryptorSettings.getInstance();
        apiKeyField.setText(settings.getApiKey());
        colorSchemeComboBox.setSelectedIndex(settings.isRedForUp() ? 0 : 1);
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }
} 