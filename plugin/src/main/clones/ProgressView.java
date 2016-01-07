package clones;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ProgressView extends DialogWrapper {

    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel label = new JLabel();
    private volatile Status status = Status.Initializing;

    private int progressValue = -1;
    private final int maxProgressValue;

    public ProgressView(@Nullable Project project, int files) {
        super(project);

        assert(files>=0);
        this.maxProgressValue = files;

        EventQueue.invokeLater(() -> {
            init();

            setTitle("Locate clones...");
            setModal(true);
            setResizable(false);

            panel.setPreferredSize(new Dimension(250, 40));

            progressBar.setMaximum(files);
            progressBar.setStringPainted(true);

            GridBagConstraints constraints = new GridBagConstraints();

            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            panel.add(progressBar, constraints);

            constraints.gridy = 1;
            panel.add(label, constraints);

            next("Preparing files...");
            status = Status.Processing;

            DoNotAskOption option = new PropertyDoNotAskOption("cancel");

            this.setDoNotAskOption(option);
        });
    }

    public Status getStatus(){
        return status;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public void done(){
        EventQueue.invokeLater(this::doOKAction);
    }

    @Override
    public void doCancelAction() {
        status = Status.Canceled;
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        status = Status.Done;
        super.doOKAction();
    }

    public void setAsProcessing(){
        EventQueue.invokeLater(() -> label.setText("Preparing data..."));
    }

    /**
     * Updating progressView like file with @param filename is processed.
     */
    public static int maxStringSize = 35;
    public void next(@NotNull final String filename){
        EventQueue.invokeLater(() -> {
            /* Обрезание строки */
            String string = (filename.length()> maxStringSize) ? filename.substring(0, maxStringSize)+"..." : filename;
            label.setText(string);
            progressBar.setValue(progressValue++);
            progressBar.setString(progressValue + "/" + maxProgressValue);
        });
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[0];
    }

    public enum Status {
        Initializing,
        Processing,
        Canceled,
        Done
    }
}
