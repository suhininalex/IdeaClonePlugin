package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProcessCanceledException
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.utils.method
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.toolwindow.CloneViewManager
import nl.komponents.kovenant.then

class ShowAllClonesAction: AnAction(PluginLabels.getLabel("menu-find-all-tooltip")) {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project?.cloneManager?.initialized ?: false
        e.presentation.text = PluginLabels.getLabel("menu-find-all-tooltip")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!
        project.cloneManager.instance.getAllFilteredClones().then {
            CloneViewManager.showClonesData(project, it)
        }.fail {
            if (it !is ProcessCanceledException)
                throw it
        }
    }
}

class ShowMethodClonesAction: AnAction(PluginLabels.getLabel("menu-find-in-clone-tooltip")){

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project?.cloneManager?.initialized ?: false
        e.presentation.text = PluginLabels.getLabel("menu-find-in-clone-text")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val method = e.getData(LangDataKeys.PSI_ELEMENT)?.method ?: return
        val project = e.project!!
        val clones = project.cloneManager.instance.getMethodFilteredClones(method)
        CloneViewManager.showClonesData(project, clones)
    }
}
