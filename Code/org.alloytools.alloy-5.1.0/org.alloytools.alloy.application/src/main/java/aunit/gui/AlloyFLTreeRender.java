package aunit.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class AlloyFLTreeRender implements TreeCellRenderer {

    private JLabel label;

    public AlloyFLTreeRender() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof AlloyFLScoreTreeNode) {
            AlloyFLScoreTreeNode node = (AlloyFLScoreTreeNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b>Suspiciousness score:</b> " + node.score);
        } else if (o instanceof AlloyFLFormulaTreeNode) {
            AlloyFLFormulaTreeNode node = (AlloyFLFormulaTreeNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b>" + node.type + ": </b>" + node.formula);
        } else if (o instanceof AlloyFLHeaderNode) {
            AlloyFLHeaderNode node = (AlloyFLHeaderNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html>The following " + node.title + " is found to be suspicious.");
        } else if (o instanceof AlloyFLHighlightNode) {
            AlloyFLHighlightNode node = (AlloyFLHighlightNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b><font color = 3E9FF4> Highlight location </html>");
        }
        label.setFont(tree.getFont());
        return label;
    }
}

