package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProcessCanceledException
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.languagescope.java.JavaIndexedSequence
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.utils.method
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.toolwindow.CloneToolwindowManager
import nl.komponents.kovenant.then

class ShowAllClonesAction: AnAction(PluginLabels.getLabel("menu-find-all-tooltip")) {

    override fun update(e: AnActionEvent) {
        with (e.presentation) {
            isEnabled = e.project?.cloneManager?.initialized ?: false
            text = PluginLabels.getLabel("menu-find-all-text")
            description = PluginLabels.getLabel("menu-find-all-description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project!!.cloneManager.instance.getAllFilteredClones()
        .then {
            CloneToolwindowManager.showClonesData(it)
        }.fail {
            if (it !is ProcessCanceledException)
                throw it
        }
    }
}

class ShowMethodClonesAction: AnAction(PluginLabels.getLabel("menu-find-in-clone-tooltip")){

    override fun update(e: AnActionEvent) {
        with (e.presentation){
            isEnabled = e.project?.cloneManager?.initialized ?: false
            text = PluginLabels.getLabel("menu-find-in-clone-text")
            description = PluginLabels.getLabel("menu-find-in-clone-description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val method = e.getData(LangDataKeys.PSI_ELEMENT)?.method ?: return
        val clones = e.project!!.cloneManager.instance.getSequenceFilteredClones(JavaIndexedSequence(method))
        CloneToolwindowManager.showClonesData(clones)
    }
}
