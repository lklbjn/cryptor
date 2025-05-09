package com.cryptor.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.cryptor.window.CryptorToolWindow;

public class RefreshAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Cryptor");
        if (toolWindow == null) {
            return;
        }
        // 获取CryptorToolWindow实例
        CryptorToolWindow cryptorToolWindow = project.getService(CryptorToolWindow.class);
        if (cryptorToolWindow != null) {
            cryptorToolWindow.refreshData();
        }
    }
} 