package com.cryptor.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CryptorToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CryptorToolWindow cryptorToolWindow = project.getService(CryptorToolWindow.class);
        Content content = ContentFactory.getInstance().createContent(
                cryptorToolWindow.getContent(),
                "Cryptor",
                false
        );
        toolWindow.getContentManager().addContent(content);
    }
} 