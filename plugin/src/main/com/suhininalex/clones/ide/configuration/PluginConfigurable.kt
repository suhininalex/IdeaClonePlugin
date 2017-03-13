package com.suhininalex.clones.ide.configuration

import com.intellij.openapi.options.Configurable
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.utils.CurrentProject
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
        }

    override fun disposeUIResources() {
        configurationPanel = null
    }

    override fun getDisplayName(): String {
        return "TreeClone finder configuration"
    }

    override fun apply() {
        configurationPanel?.apply {
            if (enableForThisProject && ! PluginSettings.enabledForProject) {
                PluginSettings.enabledForProject = enableForThisProject
                CurrentProject?.cloneManager?.initialize()
            } else if (PluginSettings.enabledForProject && ! enableForThisProject){
                PluginSettings.enabledForProject = enableForThisProject
                CurrentProject?.cloneManager?.cancel()
            }
            PluginSettings.coverageSkipFilter = skipSelfCoverageFiltration
            PluginSettings.minCloneLength = minimalCloneLength
        }
    }

    override fun createComponent(): JComponent {
        configurationPanel = ConfigurationPanel()
        return configurationPanel!!.myPanel
    }

    override fun reset() {
        configurationPanel?.apply {
            skipSelfCoverageFiltration = PluginSettings.coverageSkipFilter
            minimalCloneLength = PluginSettings.minCloneLength
            enableForThisProject = PluginSettings.enabledForProject
        }
    }

    override fun getHelpTopic(): String = "help.find-cloneClasses"

}
