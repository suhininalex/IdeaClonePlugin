package com.suhininalex.clones.ide.configuration

import com.intellij.openapi.options.Configurable
import com.suhininalex.clones.core.utils.CurrentProject
import com.suhininalex.clones.ide.CloneFinderIndex
import javax.swing.*

class PluginConfigurable : Configurable {
    var configurationPanel: ConfigurationPanel? = null

    override fun isModified(): Boolean =
        if (configurationPanel == null) {
            false
        } else with (configurationPanel!!) {
            PluginSettings.coverageSkipFilter != skipSelfCoverageFiltration
            || PluginSettings.enabledForProject != enableForThisProject
            || PluginSettings.minCloneLength != minimalCloneLength
            || PluginSettings.disableTestFolder != testFilesDisabled
            || PluginSettings.javaSearchEnabled != javaSearchEnabled
            || PluginSettings.kotlinSearchEnabled != kotlinSearchEnabled
            || PluginSettings.maxMemory != maxUsedMemory
            || PluginSettings.enableGaps != enableGaps
        }

    override fun disposeUIResources() {
        configurationPanel = null
    }

    override fun getDisplayName(): String =
        PluginLabels.getMessage("settings-title")

    override fun apply() {
        configurationPanel?.apply {
            saveSettings()
            CloneFinderIndex.rebuild(CurrentProject!!)
        }
    }

    override fun createComponent(): JComponent {
        configurationPanel = ConfigurationPanel()
        return configurationPanel!!.myPanel
    }

    override fun reset() {
        loadSettings()
    }

    override fun getHelpTopic(): String = "help.find-cloneClasses"

    private fun loadSettings(){
        configurationPanel?.apply {
            skipSelfCoverageFiltration = PluginSettings.coverageSkipFilter
            minimalCloneLength = PluginSettings.minCloneLength
            enableForThisProject = PluginSettings.enabledForProject
            testFilesDisabled = PluginSettings.disableTestFolder
            javaSearchEnabled = PluginSettings.javaSearchEnabled
            kotlinSearchEnabled = PluginSettings.kotlinSearchEnabled
            maxUsedMemory = PluginSettings.maxMemory
            enableGaps = PluginSettings.enableGaps
        }
    }

    private fun saveSettings(){
        configurationPanel?.apply {
            PluginSettings.coverageSkipFilter = skipSelfCoverageFiltration
            PluginSettings.minCloneLength = minimalCloneLength
            PluginSettings.enabledForProject = enableForThisProject
            PluginSettings.disableTestFolder = testFilesDisabled
            PluginSettings.javaSearchEnabled = javaSearchEnabled
            PluginSettings.kotlinSearchEnabled = kotlinSearchEnabled
            PluginSettings.maxMemory = maxUsedMemory
            PluginSettings.enableGaps = enableGaps
        }
    }
}