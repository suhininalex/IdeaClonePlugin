package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class CloneTreeView : Tree {

    val model: DefaultTreeModel = super.getModel() as DefaultTreeModel

    val project: Project

    val root: DefaultMutableTreeNode

    constructor(cloneClasses: List<CloneClass>){
        require(cloneClasses.isNotEmpty())
        project = cloneClasses.first().project
        root = createRootNode(cloneClasses.sortedByDescending { it.length })
        initialize()
    }

    constructor(cloneClass: CloneClass){
        project = cloneClass.project
        root = createCloneClassNode(cloneClass)
        initialize()
    }

    private val mouseListener = doubleClickListener {
        val clickedElement = getSelectionModel()?.selectionPath?.lastPathComponent
        if (clickedElement is ViewClone) {
            clickedElement.navigateToSource()
        }
    }

    fun initialize(){
        isRootVisible = false
        model.setRoot(root)
        addMouseListener(mouseListener)
    }
}

fun createRootNode(list: List<CloneClass>): DefaultMutableTreeNode =
        SwingTreeNode("root", list.map(::createCloneClassNode))