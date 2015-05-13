package Clones;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.OptionsDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by llama on 24.04.15.
 */
public class ProgressView extends DialogWrapper {

    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel label = new JLabel();
    private volatile Status status = Status.Initializing;

    private int value = -1;
    private final int size;

    public ProgressView(@Nullable Project project, int size) {
        super(project);

        assert(size>=0);
        this.size = size;
        init();

        setTitle("Обработка файлов");
        setModal(true);
        setResizable(false);

        panel.setPreferredSize(new Dimension(250, 40));

        progressBar.setMaximum(size);
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
        EventQueue.invokeLater(() -> {
            this.doOKAction();
        });
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
        EventQueue.invokeLater(() -> {
            label.setText("Preparing data...");
        });
    }

    /**
     * Updating progressView like file with @param filename is processed.
     */
    public static int maxSize=35;
    public void next(@NotNull final String filename){
        EventQueue.invokeLater(() -> {
            /* Обрезание строки */
            String string = (filename.length()>maxSize) ? filename.substring(0,maxSize)+"..." : filename;
            label.setText(string);
            progressBar.setValue(value++);
            progressBar.setString(value + "/" + size);
        });
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[0];
    }

    public static enum Status {
        Initializing,
        Processing,
        Canceled,
        Done
    }
}
