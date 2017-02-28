package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.postprocessing.scoreSelfCoverage
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.TreeClone
import com.suhininalex.clones.core.utils.textLength
import java.awt.EventQueue
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode

private class ClonesView (val clones: List<CloneClass>) : Tree() {

    val index = AtomicInteger(1)

    val mouseListener = object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2)
                getSelectionModel().selectionPath.lastPathComponent.let {
                    if (it is ViewTreeNode) it.clone.selectInEditor()
                }
        }
    }

    init {
        model = DefaultTreeModel(buildTree())
        isRootVisible = false
        addMouseListener(mouseListener)
    }

    fun buildTree(): TreeNode =
        DefaultMutableTreeNode("root").apply {
            clones.sortedByDescending { it.length }.forEach {
                this.add(it.asTreeNode())
            }
        }

    private fun CloneClass.asTreeNode() =
        DefaultMutableTreeNode("${index.andIncrement}. TreeClone class with $length tokens and ${clones.count()} duplicates. Coverage: ${clones.first().scoreSelfCoverage()}%").apply {
            clones.forEach {
                this.add(ViewTreeNode(it))
            }
        }

    fun TreeClone.asString() = firstPsi.node.elementType.toString() + " | " + lastPsi.node.elementType.toString();
}

object ClonesViewProvider {
    private fun Project.getToolWindow() =
            with (getToolWindowManager()) {
                getToolWindow("CloneFinder") ?: registerToolWindow("CloneFinder", true, ToolWindowAnchor.BOTTOM)
            }

    fun showClonesData(project: Project, clonesList: List<CloneClass>) =
        EventQueue.invokeLater {
            with (project.getToolWindow()) {
                hide(null)
                component.removeAll()
                component.add(JBScrollPane(ClonesView(clonesList)))
                show(null)
            }
        }
}

fun Project.getToolWindowManager(): ToolWindowManager =
        ToolWindowManager.getInstance(this)

val CloneClass.length: Int
    get() = clones.first().textLength