package com.suhininalex.clones.ide.toolwindow

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.DiffRequestFactory
import com.intellij.diff.DiffRequestPanel
import com.intellij.diff.contents.DiffContent
import com.intellij.diff.requests.SimpleDiffRequest
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.utils.document
import com.suhininalex.clones.core.utils.printText
import com.suhininalex.clones.core.utils.project
import com.suhininalex.clones.core.utils.textRange
import java.awt.Component
import java.awt.FlowLayout
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URL
import javax.swing.*

fun createPanel(clones: List<Clone>): JPanel {
    val duplicateToolwindow = DuplicateToolwindow()
    val cloneReports = clones.map(::CloneReport)
    duplicateToolwindow.reportPanel.apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        cloneReports.forEach {
            add(it)
        }
    }
    val diffController = DiffController(cloneReports[0], cloneReports[1])
    cloneReports.forEach {
        it.leftButton.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        diffController.left = it
                    }
                }
        )
        it.rightButton.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        diffController.right = it
                    }
                }
        )
    }
    duplicateToolwindow.diffPanel.add(diffController.component)
    return duplicateToolwindow.panel
}

fun loadResource(path: String): URL? = CloneToolwindowManager::class.java.classLoader.getResource(path)

fun ImageIcon.rescale(width: Int, height: Int) =
        ImageIcon(image.getScaledInstance(width, height, Image.SCALE_DEFAULT))

//IconLoader
val normalLeftImage = ImageIcon(loadResource("img/left.png")).rescale(16,16)
val hoverLeftImage = ImageIcon(loadResource("img/left-hover.png")).rescale(16,16)
val selectedLeftImage = ImageIcon(loadResource("img/left-select.png")).rescale(16,16)

fun createLeftButton() = ImageButton(normalLeftImage, hoverLeftImage, selectedLeftImage)

val normalRightImage = ImageIcon(loadResource("img/right.png")).rescale(16,16)
val hoverRightImage = ImageIcon(loadResource("img/right-hover.png")).rescale(16,16)
val selectedRightImage = ImageIcon(loadResource("img/right-select.png")).rescale(16,16)

fun createRightButton() = ImageButton(normalRightImage, hoverRightImage, selectedRightImage)

class CloneReport(val clone: Clone): JPanel() {
    val leftButton: ImageButton = createLeftButton()
    val rightButton: ImageButton = createRightButton()
    val description: JLabel = JLabel(clone.description)

    init{
        layout = FlowLayout(FlowLayout.LEFT)
        add(leftButton)
        add(rightButton)
        add(description)
        description.addMouseListener(object: MouseAdapter(){
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount >= 2) clone.navigateToSource()
            }
        })
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

    val component: Component
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