package aunit.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class MuAlloyTreeRender implements TreeCellRenderer {

    private JLabel label;

    public MuAlloyTreeRender() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof MuAlloyTreeNode) {
            MuAlloyTreeNode node = (MuAlloyTreeNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b><font color = 3E9FF4> View mutant killing test case </html>");
        } else if (o instanceof MuAlloyHeaderNode) {
            MuAlloyHeaderNode node = (MuAlloyHeaderNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b>Mutant: </b>" + node.content);
        }
        label.setFont(tree.getFont());
        return label;
    }
}

