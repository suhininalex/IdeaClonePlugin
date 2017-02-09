package com.suhininalex.clones.ide.configuration

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.BoxLayout
import javax.swing.JLabel


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
            layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
            add(JLabel("Label sample"))

        }
    }

    override fun reset() {
        println("RESEEET")
    }

    override fun getHelpTopic(): String? = null

}
