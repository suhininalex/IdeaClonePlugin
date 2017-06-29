package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.utils.Application
import com.suhininalex.clones.core.utils.readAction
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import com.suhininalex.clones.ide.toolwindow.CloneToolwindowManager
import nl.komponents.kovenant.task
import nl.komponents.kovenant.then

class ShowAllClonesAction: AnAction(PluginLabels.getLabel("menu-find-all-tooltip")) {

    override fun update(e: AnActionEvent) {
        with (e.presentation) {
            isVisible = PluginSettings.enabledForProject
            text = PluginLabels.getLabel("menu-find-all-text")
            description = PluginLabels.getLabel("menu-find-all-description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        CloneFinderIndex.enshureUpToDate(e.project!!)
//        Application.executeOnPooledThread {
//            ProgressManager.getInstance().run(object : Task.Modal(e.project, "asdf", false){
//                override fun run(p0: ProgressIndicator) {
//                    val max = 100000000
//                    for (i in 1..max) {
//                        p0.fraction = i / max.toDouble()
//                    }
//                }
//            })
//
//            Application.readAction {
//                val clones = CloneIndexer.getAllCloneClasses().toList().filterSubClassClones().notLongestSequenceFilter().splitSiblingClones().mergeCloneClasses().filterSelfCoveredClasses()
//                CloneToolwindowManager.showClonesData(clones)
//            }
//        }

        task {
            CloneIndexer.getAllFilteredClones().get()
        }
        .then {
            CloneToolwindowManager.showClonesData(it)
        }.fail {
            if (it !is ProcessCanceledException)
                throw it
        }
    }
}