package Clones;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.suhininalex.clones.Clone;
import com.suhininalex.clones.CloneClass;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;

public final class ClonesView extends Tree {
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    private final DefaultTreeModel model = new DefaultTreeModel(root);

    private ClonesView(@NotNull final List<CloneClass> clones){
        Collections.sort(clones, CloneClass.getLengthComparator());
        for (CloneClass cloneClass : clones){
            this.add(cloneClass);
        }
        this.setModel(model);
        this.setRootVisible(false);
        this.addMouseListener(mouseListener);
    }

    private void add(CloneClass cloneClass) {
        DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("Clone class with " + cloneClass.size() + " tokens and " + cloneClass.size() + " duplicates.");
        root.add(classNode);
        for (Clone range : cloneClass.getClones()) {
            JTreeCloneNode clone = new JTreeCloneNode(range);
            classNode.add(clone);
        }
    }

    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount()==2){
                Object selectedNode = getSelectionModel().getSelectionPath().getLastPathComponent();
                if (selectedNode instanceof JTreeCloneNode)
                ((JTreeCloneNode) selectedNode).selectInEditor();
            }
        }
    };

    @NotNull
    private static synchronized ToolWindow getToolWindow(Project project){
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("CloneFinder");
        if (window==null) window = ToolWindowManager.getInstance(project).registerToolWindow("CloneFinder", true, ToolWindowAnchor.BOTTOM);
        return window;
    }

    public static synchronized void showClonesData(Project project, List<CloneClass> clonesList){
        final ClonesView clonesView = new ClonesView(clonesList);
        EventQueue.invokeLater(() -> {
            ToolWindow toolWindow = getToolWindow(project);
            toolWindow.hide(null);
            JComponent window = toolWindow.getComponent();
            window.removeAll();
            JBScrollPane pane = new JBScrollPane(clonesView);
            window.add(pane);
            toolWindow.show(null);
        });
    }
}
