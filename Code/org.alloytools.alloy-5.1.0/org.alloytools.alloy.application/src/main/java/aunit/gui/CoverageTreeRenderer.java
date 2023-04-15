package aunit.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class CoverageTreeRenderer implements TreeCellRenderer {

    private JLabel label;

    public CoverageTreeRenderer() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof CoverageLeafNode) {
            CoverageLeafNode node = (CoverageLeafNode) o;
            if (node.coverage_color.equals("green")) {
                label.setIcon(OurUtil.loadIcon("images/green-dot.png"));
                label.setText("<html>" + node.coverage_content + " is <font color = green><b> covered.</html>");
            } else {
                label.setIcon(OurUtil.loadIcon("images/red-dot.png"));
                label.setText("<html>" + node.coverage_content + " is <font color = red><b> not covered.</html>");
            }
        } else if (o instanceof CoverageTreeNode) {
            CoverageTreeNode node = (CoverageTreeNode) o;
            if (node.coverage_color.equals("green")) {
                label.setIcon(OurUtil.loadIcon("images/green-dot.png"));
            } else if (node.coverage_color.equals("yellow")) {
                label.setIcon(OurUtil.loadIcon("images/yellow-dot.png"));
            } else {
                label.setIcon(OurUtil.loadIcon("images/red-dot.png"));
            }

            if (node.line_start.contains(",") || node.line_start.contains(" - "))
                label.setText("<html>" + node.coverage_content + " <b>(lines " + node.line_start + ")</b></html>");
            else
                label.setText("<html>" + node.coverage_content + " <b>(line " + node.line_start + ")</b></html>");
        } else {
            label.setText("<html>" + o.toString() + "</html>");
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
        }
        label.setFont(tree.getFont());
        return label;
    }

}
