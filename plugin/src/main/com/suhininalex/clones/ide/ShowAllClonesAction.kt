package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.utils.method
import com.suhininalex.clones.ide.toolwindow.CloneViewManager
import nl.komponents.kovenant.then

abstract class CloneMenuAction(text: String) : AnAction(text){
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project?.cloneManager?.initialized ?: false
    }
}

class ShowAllClonesAction: CloneMenuAction("Find all clones in project") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!
        project.cloneManager.instance.getAllFilteredClones().then {
            CloneViewManager.showClonesData(project, it)
        }.fail {
            throw it
        }
    }
}

class ShowMethodClonesAction: CloneMenuAction("Find all clones related to this method"){

    override fun actionPerformed(e: AnActionEvent) {
        val method = e.getData(LangDataKeys.PSI_ELEMENT)?.method ?: return
        val project = e.project!!
        val clones = project.cloneManager.instance.getMethodFilteredClones(method)
        CloneViewManager.showClonesData(project, clones)
    }
}
