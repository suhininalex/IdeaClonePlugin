package clones

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.suhininalex.clones.abbrevate

import javax.swing.*
import java.awt.*

class ProgressView(val project: Project, val maxProgressValue: Int) : DialogWrapper(project) {

    private val panel = JPanel(GridBagLayout())
    private val progressBar = JProgressBar()
    private val label = JLabel("Preparing files...")
    @Volatile var status = Status.Initializing

    private var progressValue = 0

    init {
        EventQueue.invokeLater {
            init()

            title = "Locate clones..."
            isModal = false
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

    override fun doCancelAction() {
        status = Status.Canceled
        super.doCancelAction()
    }

    override fun doOKAction() {
        status = Status.Done
        super.doOKAction()
    }

    fun setAsProcessing() =
        EventQueue.invokeLater { label.text = "Preparing data..." }

    fun next(filename: String) =
        EventQueue.invokeLater {
            label.text = filename.abbrevate(35)
            progressBar.value = progressValue++
            progressBar.string = "$progressValue/$maxProgressValue"
        }

    override fun createActions(): Array<Action?> {
        return arrayOfNulls(0)
    }

    enum class Status {
        Initializing, Processing, Canceled, Done
    }
}
