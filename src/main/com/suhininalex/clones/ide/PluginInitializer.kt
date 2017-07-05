package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.suhininalex.clones.core.utils.Logger
import com.suhininalex.clones.core.utils.addBulkFileListener

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        Logger.log("[Initializer] Startup")
        project.addBulkFileListener(FileListener())
        CloneFinderIndex.rebuild(project)
    }
}