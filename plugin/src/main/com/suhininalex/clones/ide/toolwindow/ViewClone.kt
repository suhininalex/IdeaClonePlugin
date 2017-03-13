package com.suhininalex.clones.ide.toolwindow

import com.intellij.ide.SelectInEditorManager
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.*
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
            userObject = "INVALID"
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
    get() = "Clone class with $length tokens and $size duplicates."

val Clone.description: String
    get() = "Lines ${firstPsi.startLine} to ${lastPsi.endLine} from ${file.presentableName}"
