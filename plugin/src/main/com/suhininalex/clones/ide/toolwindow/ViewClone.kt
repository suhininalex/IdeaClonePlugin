package com.suhininalex.clones.ide.toolwindow

import com.intellij.ide.SelectInEditorManager
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import java.awt.EventQueue
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Leaf node in JTree
 * This class represents a duplicate
 */
class ViewClone(val clone: Clone) : DefaultMutableTreeNode(clone.description){
    var valid = true
        private set

    fun invalidate(){
        valid = false
        EventQueue.invokeLater {
            userObject = PluginLabels.getLabel("toolwindow-clone-invalid")
        }
    }

    fun navigateToSource(){
        if (! valid) return
        with(clone){
            SelectInEditorManager.getInstance(project).selectInEditor(
                    file,
                    textRange.startOffset,
                    textRange.endOffset,
                    false,
                    false
            )
        }
    }

}

/**
 * Create tree node for a clone class
 */
fun createCloneClassNode(cloneClass: CloneClass): DefaultMutableTreeNode =
    SwingTreeNode(cloneClass.description, cloneClass.clones.map(::ViewClone).toList())

private val CloneClass.description: String
    get() = PluginLabels.getLabel("toolwindow-class-node")
            .replace("\$length", "$length")
            .replace("\$size", "$size")

val Clone.description: String
    get() = PluginLabels.getLabel("toolwindow-clone-node")
            .replace("startLine", "${firstPsi.startLine}")
            .replace("endLine", "${lastPsi.endLine}")
            .replace("file", file.presentableName)