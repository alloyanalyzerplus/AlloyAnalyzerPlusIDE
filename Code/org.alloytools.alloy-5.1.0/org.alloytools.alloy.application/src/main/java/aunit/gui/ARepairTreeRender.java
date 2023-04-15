package aunit.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class ARepairTreeRender implements TreeCellRenderer {

    private JLabel label;

    public ARepairTreeRender() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof ARepairTreeNode) {
            ARepairTreeNode node = (ARepairTreeNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b><font color = 3E9FF4> View patched model </html>");
        }
        label.setFont(tree.getFont());
        return label;
    }
}

