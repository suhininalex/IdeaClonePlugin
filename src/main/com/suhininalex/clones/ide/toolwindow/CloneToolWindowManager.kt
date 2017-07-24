package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.description
import com.suhininalex.clones.core.utils.project
import com.suhininalex.clones.core.utils.toolWindowManager
import com.suhininalex.clones.ide.configuration.PluginLabels
import java.awt.EventQueue

object CloneToolwindowManager {

    private val toolwindowId = PluginLabels.getMessage("toolwindow-id")

    fun showClonesData(cloneClass: CloneClass) {
        EventQueue.invokeLater {
            with(cloneClass.project.cloneToolWindow) {
                val content =  with (contentManager.factory) {
                    createContent(createPanel(cloneClass.clones.toList()), cloneClass.description, false)
                }
                contentManager.addContent(content)
                show{}
            }
        }
    }

    private val Project.cloneToolWindow: ToolWindow
        get() = toolWindowManager.getToolWindow(toolwindowId)
                ?: toolWindowManager.registerToolWindow(toolwindowId, true, ToolWindowAnchor.BOTTOM)
}
