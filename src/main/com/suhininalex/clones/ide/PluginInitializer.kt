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
        project.addBulkFileListener(FileListener())
        CloneFinderIndex.rebuild(project)
    }
}