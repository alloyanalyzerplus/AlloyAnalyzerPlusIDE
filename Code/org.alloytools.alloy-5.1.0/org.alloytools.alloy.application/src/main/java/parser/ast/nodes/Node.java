package parser.ast.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import parser.ast.visitor.Visitable;
import parser.etc.NodeMap;

public abstract class Node implements Visitable, Serializable {

    private static final long         serialVersionUID = -894430165150618198L;
    protected Node                    parent;
    /**
     * The direct children of the node. Note that this field may be illegal or
     * incorrect if the user invokes setters or modify the internal data structure.
     * For example, if the user invoke getPredDeclList().add(pred), then pred will
     * not appear in the children field. Similarly, if the user invoke
     * setPredDeclList(preds), then the new preds will not be shown in the children.
     */
    protected List<Node>              children;
    /**
     * Maps the Alloy AST nodes to AParser AST nodes. All AParser AST nodes keep the
     * same node map.
     */
    protected transient final NodeMap nodeMap;

    public Node(Node parent, NodeMap nodeMap) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.nodeMap = nodeMap;
    }

    public Node(Node parent) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.nodeMap = parent == null ? null : parent.nodeMap;
    }

    public Node() {
        this.parent = null;
        this.children = null;
        this.nodeMap = null;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public NodeMap getNodeMap() {
        return nodeMap;
    }

    public void addToNodeMap(Object src, Node dst) {
        if (nodeMap == null) {
            return;
        }
        nodeMap.addToMap(src, dst);
    }

    public List<Node> getChildren() {
        return children;
    }
}
