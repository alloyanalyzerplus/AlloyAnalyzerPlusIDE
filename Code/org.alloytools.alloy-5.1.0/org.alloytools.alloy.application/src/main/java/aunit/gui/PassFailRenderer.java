package aunit.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class PassFailRenderer implements TreeCellRenderer {

    private JLabel label;

    public PassFailRenderer() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof PassFailTreeNode) {
            PassFailTreeNode node = (PassFailTreeNode) o;
            if (node.result.equals("pass")) {
                label.setIcon(OurUtil.loadIcon("images/pass.png"));
                String[] text = node.name.split("passes");
                label.setText("<html>" + text[0] + "<font color=green><b> passes" + text[1] + "</b></html>");
            } else if (node.result.equals("fail")) {
                label.setIcon(OurUtil.loadIcon("images/fail.png"));
                String[] text = node.name.split("fails");
                label.setText("<html>" + text[0] + "<font color=red><b> fails" + text[1] + "</b></html>");
            } else if (node.result.equals("error")) {
                label.setIcon(OurUtil.loadIcon("images/error.png"));
                String[] text = node.name.split("error");
                label.setText("<html>" + text[0] + "<font color=maroon><b> error." + text[1] + "</b></html>");
            }

        } else if (o instanceof AUnitTreeNode) {
            AUnitTreeNode node = (AUnitTreeNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            if (node.linkDestination != null) {
                label.setText("<html><b><font color = 3E9FF4> View valuation </html>");
            } else {
                label.setText("<html><b>Command:</b> " + node.content + "</html>");
            }
        } else if (o instanceof CommandHeaderNode) {
            CommandHeaderNode node = (CommandHeaderNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html><b>" + node.content);
        } else if (o instanceof CommandFormulaNode) {
            CommandFormulaNode node = (CommandFormulaNode) o;
            label.setIcon(OurUtil.loadIcon("images/rightarrow.png"));
            label.setText(node.content);
        } else if (o instanceof CommandFactFormulaNode) {
            CommandFactFormulaNode node = (CommandFactFormulaNode) o;
            label.setIcon(OurUtil.loadIcon("images/dotdot.png"));
            label.setText(node.content);
        } else if (o instanceof CallHeaderNode) {
            CallHeaderNode node = (CallHeaderNode) o;
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText(node.content);
        } else if (o instanceof FuncInvNode) {
            FuncInvNode node = (FuncInvNode) o;
            label.setIcon(OurUtil.loadIcon("images/rightarrow.png"));
            label.setText(node.content);
        } else {
            label.setIcon(OurUtil.loadIcon("images/arrow.png"));
            label.setText("<html>" + (String) o + "</html>");
        }
        label.setFont(tree.getFont());
        return label;
    }
}

