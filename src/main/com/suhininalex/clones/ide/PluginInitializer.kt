package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiManager
import com.suhininalex.clones.core.cloneManager

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        project.cloneManager.initialize()
        val treeChangeListener = TreeChangeListener(project)
        PsiManager.getInstance(project).addPsiTreeChangeListener(treeChangeListener)
    }
}