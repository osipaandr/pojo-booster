package ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SettingsForm {
    private JCheckBox useLombokCheckBox;
    private JPanel settingsPanel;

    public SettingsForm(@NotNull Boolean useLombok) {
        if (useLombokCheckBox != null) {
            useLombokCheckBox.setSelected(useLombok);
        }
    }

    public Boolean useLombok() {
        return useLombokCheckBox.isSelected();
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }
}
