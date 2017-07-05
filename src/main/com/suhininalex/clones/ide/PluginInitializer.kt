package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, FileListener())
        CloneFinderIndex.rebuild()
    }
}