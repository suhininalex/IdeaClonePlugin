package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProcessCanceledException
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import com.suhininalex.clones.ide.toolwindow.CloneToolwindowManager
import com.suhininalex.clones.ide.toolwindow.indexedSequence
import nl.komponents.kovenant.then

class ShowAllClonesAction: AnAction(PluginLabels.getLabel("menu-find-all-tooltip")) {

    override fun update(e: AnActionEvent) {
        with (e.presentation) {
            isVisible = PluginSettings.enabledForProject
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

class ShowMethodClonesAction: AnAction(PluginLabels.getLabel("menu-find-in-scope-tooltip")){

    override fun update(e: AnActionEvent) {
        with (e.presentation){
            isVisible = PluginSettings.enabledForProject
            val initialized = e.project?.cloneManager?.initialized ?: false
            val indexedSequence = e.getData(LangDataKeys.PSI_ELEMENT)?.indexedSequence
            isEnabled = initialized && indexedSequence != null
            text = PluginLabels.getLabel("menu-find-in-scope-text")
            description = PluginLabels.getLabel("menu-find-in-scope-description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val indexedSequence = e.getData(LangDataKeys.PSI_ELEMENT)?.indexedSequence ?: return
        val clones = e.project!!.cloneManager.instance.getSequenceFilteredClones(indexedSequence)
        CloneToolwindowManager.showClonesData(clones)
    }
}
