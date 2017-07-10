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
import com.suhininalex.clones.ide.configuration.PluginLabels
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
        val estimatedMemory = files/10 //in Mb
        if (estimatedMemory > 500){
            Application.invokeLater {
                Notifications.Bus.notify(createMemoryNotification(estimatedMemory))
            }
        }
    }
}

fun createMemoryNotification(megabytes: Int): Notification {
    val title = PluginLabels.getLabel("warning-memory-issue-title")
    val message = PluginLabels.getLabel("warning-memory-issue-message").replace("\$maxMemory", megabytes.toString())
    return Notification("Actions", title, message, NotificationType.WARNING)
}