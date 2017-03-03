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
        return "TreeClone finder configuration"
    }

    override fun apply() {
        println("Apply!")
    }

    override fun createComponent(): JComponent {

        val minTokenLengthField = JFormattedTextField(50)

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            val LengthClone = JPanel(BorderLayout()).apply {
                val label = JLabel("Minimal length of detected clone")
                add(label, BorderLayout.WEST)
                label.labelFor = minTokenLengthField
                val afterLabel = JLabel(" tokens")
                add(afterLabel, BorderLayout.EAST)
                afterLabel.labelFor = minTokenLengthField
                add(minTokenLengthField)
            }

            add(Box.createVerticalStrut(10))
            add(LengthClone)

//            val LengthClone2 = JPanel(BorderLayout()).apply {
//                add(JLabel("Minimal length of detected clone"))
//                add(JFormattedTextField(50))
//                add(JLabel(" symbols"))
//            }
//
//            add(Box.createVerticalStrut(3));
//            add(LengthClone2)
        }
    }

    override fun reset() {
    }

    override fun getHelpTopic(): String? = null

}
