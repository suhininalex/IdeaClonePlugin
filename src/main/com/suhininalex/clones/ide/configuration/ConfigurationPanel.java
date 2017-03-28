package com.suhininalex.clones.ide.configuration;

import javax.swing.*;

public class ConfigurationPanel {
    JPanel myPanel;
    private JSpinner minimalCloneLengthSpinner;
    private JSpinner skipSelfCoverageFiltrationSpinner;
    private JCheckBox enableForThisProjectCheckbox;
    private JCheckBox testFilesDisabled;
    private JCheckBox javaSearchEnabled;
    private JCheckBox kotlinSearchEnabled;

    public ConfigurationPanel() {
        minimalCloneLengthSpinner.setModel(new SpinnerNumberModel(15, 15, 1000, 5));
        skipSelfCoverageFiltrationSpinner.setModel(new SpinnerNumberModel(10, 10, 200, 5));
    }

    public int getMinimalCloneLength() {
        return (int) minimalCloneLengthSpinner.getValue();
    }

    public void setMinimalCloneLength(int value) {
        minimalCloneLengthSpinner.setValue(value);
    }

    public int getSkipSelfCoverageFiltration() {
        return (int) skipSelfCoverageFiltrationSpinner.getValue();
    }

    public void setSkipSelfCoverageFiltration(int value) {
         skipSelfCoverageFiltrationSpinner.setValue(value);
    }

    public boolean getEnableForThisProject() {
        return enableForThisProjectCheckbox.isSelected();
    }

    public void setEnableForThisProject(boolean value) {
        enableForThisProjectCheckbox.setSelected(value);
    }

    public boolean getTestFilesDisabled() {
        return testFilesDisabled.isSelected();
    }

    public void setTestFilesDisabled(boolean value) {
        testFilesDisabled.setSelected(value);
    }

    public boolean getJavaSearchEnabled() {
        return javaSearchEnabled.isSelected();
    }

    public void setJavaSearchEnabled(boolean value) {
        javaSearchEnabled.setSelected(value);
    }

    public boolean getKotlinSearchEnabled() {
        return kotlinSearchEnabled.isSelected();
    }

    public void setKotlinSearchEnabled(boolean value) {
        kotlinSearchEnabled.setSelected(value);
    }
}
