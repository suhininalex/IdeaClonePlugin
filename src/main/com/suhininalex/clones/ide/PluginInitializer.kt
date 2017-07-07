package com.suhininalex.clones.ide

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.suhininalex.clones.core.utils.Application
import com.suhininalex.clones.core.utils.Logger
import com.suhininalex.clones.core.utils.addBulkFileListener
import com.suhininalex.clones.core.utils.sourceFiles
import nl.komponents.kovenant.task
import nl.komponents.kovenant.then

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        Logger.log("[Initializer] Startup")
        showMemoryWarning(project)
        project.addBulkFileListener(FileListener())
        CloneFinderIndex.rebuild(project)
    }
}

fun showMemoryWarning(project: Project){
    task {
        project.sourceFiles.count()
    }.then { files ->
        if (files > 5000){
            val maxMemory = files/10
            Application.invokeLater {
                Notifications.Bus.notify(Notification("Actions", "Clone finder: memory issue", "Plugin may consume up to $maxMemory Mb RAM\n", NotificationType.WARNING))
            }
        }
    }
}