package Clones;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by llama on 24.04.15.
 */
public class ProgressView extends DialogWrapper {

    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel label = new JLabel();

    private int value = -1;
    private final int size;

    public ProgressView(@Nullable Project project, int size) {
        super(project);

        assert(size>=0);
        this.size = size;
        init();

        setTitle("Обработка файлов");
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
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public static int maxSize=40;
    public void next(String filename){
        if (filename.length()>maxSize) filename = filename.substring(0,maxSize-4)+"...";
        label.setText(filename);
        progressBar.setValue(value++);
        progressBar.setString(value+"/"+size);
    }
}
