package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiManager
import com.suhininalex.clones.core.getCloneManager

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        project.getCloneManager().initialize()
        val treeChangeListener = TreeChangeListener(project)
        PsiManager.getInstance(project).addPsiTreeChangeListener(treeChangeListener)
    }
}
