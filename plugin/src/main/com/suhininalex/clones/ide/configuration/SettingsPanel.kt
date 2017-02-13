package com.suhininalex.clones.ide.configuration

import com.intellij.openapi.options.Configurable
import java.awt.BorderLayout
import javax.swing.*


class SettingsPanel: Configurable {
    override fun isModified(): Boolean {
        return false
    }

    override fun disposeUIResources() {
    }

    override fun getDisplayName(): String {
        return "Clone finder configuration"
    }

    override fun apply() {
        println("Apply!")
    }

    override fun createComponent(): JComponent {
        return JPanel().apply {
            add(JLabel("Minimal length of detected clone"), BorderLayout.AFTER_LAST_LINE)
            add(JFormattedTextField(50), BorderLayout.LINE_END)
            add(JLabel(" symbols"), BorderLayout.LINE_END)

            add(JLabel("Minimal length of detected clone"), BorderLayout.AFTER_LAST_LINE)
            add(JFormattedTextField(50), BorderLayout.LINE_END)
            add(JLabel(" symbols"), BorderLayout.LINE_END)
        }
    }

    override fun reset() {
    }

    override fun getHelpTopic(): String? = null

}
