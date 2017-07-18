package com.suhininalex.clones.ide.toolwindow

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.DiffRequestFactory
import com.intellij.diff.DiffRequestPanel
import com.intellij.diff.contents.DiffContent
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.JBSplitter
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBSlidingPanel
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.PluginIcons
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URL
import javax.swing.*

fun createPanel(clones: List<Clone>): JPanel {
    val panel = OnePixelSplitter()
    val cloneReports = clones.map(::CloneReport)
    val left = createLeftPanel(cloneReports)
    val diffController = DiffController(left = cloneReports[0], right = cloneReports[1])
    assignDiffMouseHandlers(diffController, cloneReports)
    panel.firstComponent = JScrollPane(left)
    panel.secondComponent = diffController.component
    panel.proportion = 0.20f
    return panel
}

fun createLeftPanel(cloneReports: List<CloneReport>) = JPanel().apply {
    layout = VerticalFlowLayout()
    cloneReports.forEach {
        add(it)
    }
}

fun assignDiffMouseHandlers(diffController: DiffController, cloneReports: List<CloneReport>){
    cloneReports.forEach {
        it.leftButton.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (it.clone.hasValidElements){
                            diffController.left = it
                        } else {
                            it.invalidateClone()
                        }
                    }
                }
        )
        it.rightButton.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (it.clone.hasValidElements) {
                            diffController.right = it
                        } else {
                            it.invalidateClone()
                        }
                    }
                }
        )
    }
}

class CloneReport(val clone: Clone): JPanel() {

    val leftButton: ImageButton = with (PluginIcons) {
        ImageButton(ARROW_LEFT_DEFAULT, ARROW_LEFT_HOVER, ARROW_LEFT_SELECTED)
    }

    val rightButton: ImageButton = with (PluginIcons) {
        ImageButton(ARROW_RIGHT_DEFAULT, ARROW_RIGHT_HOVER, ARROW_RIGHT_SELECTED)
    }

    val description: JLabel = JLabel(clone.description)

    val dblClickListener = doubleClickListener {
        if (clone.hasValidElements) clone.navigateToSource()
        else invalidateClone()
    }

    fun invalidateClone(){
        description.text = "INVALID"
        description.foreground = Color.RED
    }

    init{
        layout = FlowLayout(FlowLayout.LEFT)
        add(leftButton)
        add(rightButton)
        add(description)
        description.addMouseListener(dblClickListener)
    }
}

class DiffController(left: CloneReport, right: CloneReport){
    private val diffPanel: DiffRequestPanel = DiffManager.getInstance().createRequestPanel(null, {}, null)

    var left: CloneReport = left
        set(value) {
            if (field === value) return
            field.leftButton.selected = false
            field = value
            value.leftButton.selected = true
            update()
        }

    var right: CloneReport = right
        set(value) {
            if (field === value) return
            right.rightButton.selected = false
            field = value
            value.rightButton.selected = true
            update()
        }

    init {
        left.leftButton.selected = true
        right.rightButton.selected = true
        update()
    }

    val component: JComponent
        get() = diffPanel.component

    private val leftContent: DiffContent
        get() = createDiffContent(left.clone)

    private val rightContent: DiffContent
        get() = createDiffContent(right.clone)

    private fun update(){
        val request = SimpleDiffRequest("Diff", leftContent, rightContent, left.clone.description, right.clone.description)
        diffPanel.setRequest(request)
    }

    private fun createDiffContent(clone: Clone): DiffContent {
        return DiffContentFactory.getInstance().createFragment(clone.project, clone.firstPsi.document, clone.textRange)
    }

}