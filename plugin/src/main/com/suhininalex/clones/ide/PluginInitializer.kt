package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiManager

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        ProjectClonesInitializer.initializeCloneManager(project)
        val treeChangeListener = TreeChangeListener(project)
        PsiManager.getInstance(project).addPsiTreeChangeListener(treeChangeListener)
    }
}
