package clones

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import net.suhininalex.kotlin.utils.abbrevate
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Action
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar

class ProgressView(val project: Project, val maxProgressValue: Int) : DialogWrapper(project) {

    private val panel = JPanel(GridBagLayout())
    private val progressBar = JProgressBar()
    private val label = JLabel("Preparing files...")

    private var progressValue = 0

    init {
        EventQueue.invokeLater {
            init()

            title = "Locate clones..."
            isModal = true
            setResizable(false)

            createContent()

            setDoNotAskOption(DialogWrapper.PropertyDoNotAskOption("cancel"))
        }
    }

    private fun createContent(){
        panel.preferredSize = Dimension(250, 40)

        progressBar.maximum = maxProgressValue
        progressBar.isStringPainted = true
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.weightx = 1.0
        panel.add(progressBar, constraints)

        constraints.gridy = 1
        panel.add(label, constraints)
    }

    override fun createCenterPanel() = panel

    fun done() = EventQueue.invokeLater { this.doOKAction() }

    fun cancel() = EventQueue.invokeLater { this.doCancelAction() }

    fun next(filename: String) =
        EventQueue.invokeLater {
            label.text = filename.abbrevate(35)
            progressBar.value = progressValue++
            progressBar.string = "$progressValue/$maxProgressValue"
        }

    override fun createActions(): Array<Action?> = arrayOfNulls(0)

}
