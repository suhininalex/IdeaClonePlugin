package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.Content
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.toolWindowManager
import com.suhininalex.clones.ide.configuration.PluginLabels
import java.awt.EventQueue

object CloneToolwindowManager {

    fun showClonesData(clonesList: List<CloneClass>) {
        if (clonesList.isEmpty()) return
        EventQueue.invokeLater {
            showClones(CloneTreeView(clonesList), clonesList.description)
        }
    }

    fun showClonesData(cloneClass: CloneClass) {
        EventQueue.invokeLater {
            showClones(CloneTreeView(cloneClass), cloneClass.description)
        }
    }

    private val Project.cloneToolWindow: ToolWindow
        get() = toolWindowManager.getToolWindow(toolwindowId)
                ?: toolWindowManager.registerToolWindow(toolwindowId, true, ToolWindowAnchor.BOTTOM)

    private fun showClones(cloneTreeView: CloneTreeView, name: String) =
            with (cloneTreeView.project.cloneToolWindow) {
                val content = createContent(cloneTreeView, name)
                contentManager.addContent(content)
                show(null)
            }

    private fun createContent(cloneTreeView: CloneTreeView, name: String): Content {
        val factory = cloneTreeView.project.cloneToolWindow.contentManager.factory
        val content = factory.createContent(JBScrollPane(cloneTreeView), name, false)
        val invalidator = PsiTreeListener(ViewNodeInvalidator(cloneTreeView))
        content.isCloseable = true
        content.disposer = invalidator
        return content
    }

    private val CloneClass.description: String
        get() = PluginLabels.getLabel("toolwindow-tab-name").replace("\$size", "method")

    private val List<CloneClass>.description: String
        get() = PluginLabels.getLabel("toolwindow-tab-name").replace("\$size", "$size")

    private val toolwindowId = PluginLabels.getLabel("toolwindow-id")
}
