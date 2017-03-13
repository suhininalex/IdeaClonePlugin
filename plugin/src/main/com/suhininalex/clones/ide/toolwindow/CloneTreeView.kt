package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.psi.PsiManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.*
import java.awt.EventQueue
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import kotlin.properties.Delegates

class CloneTreeView : Tree, Disposable {

    val model: DefaultTreeModel = super.getModel() as DefaultTreeModel

    val project: Project

    val root: DefaultMutableTreeNode

    private var changeListener: PsiTreeListener by Delegates.notNull()

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
        val clickedElement = getSelectionModel().selectionPath.lastPathComponent
        if (clickedElement is ViewClone) {
            clickedElement.navigateToSource()
        }
    }

    fun initialize(){
        isRootVisible = false
        model.setRoot(root)
        addMouseListener(mouseListener)
        changeListener = PsiTreeListener(ViewNodeInvalidator(this))
        PsiManager.getInstance(project).addPsiTreeChangeListener(changeListener)
    }

    override fun dispose() {
        PsiManager.getInstance(project).removePsiTreeChangeListener(changeListener)
    }
}

fun createRootNode(list: List<CloneClass>): DefaultMutableTreeNode =
        SwingTreeNode("root", list.map(::createCloneClassNode))

object CloneViewManager {

    private val Project.cloneToolWindow: ToolWindow
        get() = toolWindowManager.getToolWindow("CloneFinder")
                ?: toolWindowManager.registerToolWindow("CloneFinder", true, ToolWindowAnchor.BOTTOM)

    private fun showClones(project: Project, cloneTreeView: CloneTreeView) =
        with (project.cloneToolWindow) {
            hide(null)
            component.removeAll()
            val view = JBScrollPane(cloneTreeView)
            component.add(view)
            show(null)
        }

    fun showClonesData(project: Project, clonesList: List<CloneClass>) {
        EventQueue.invokeLater {
            showClones(project, CloneTreeView(clonesList))
        }
    }

    fun showClonesData(project: Project, cloneClass: CloneClass) {
        EventQueue.invokeLater {
            showClones(project, CloneTreeView(cloneClass))
        }
    }

}

