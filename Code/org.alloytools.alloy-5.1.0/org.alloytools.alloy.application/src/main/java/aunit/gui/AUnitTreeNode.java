package aunit.gui;

public class AUnitTreeNode {

    String        content;
    public String test_name;
    public String linkDestination;

    public AUnitTreeNode(String content, String test_name) {
        this.content = content;
        this.test_name = test_name;
        linkDestination = null;
    }

    public AUnitTreeNode(String content, String linkDestination, String test_name) {
        this.content = content;
        this.test_name = test_name;
        this.linkDestination = linkDestination;
    }
}
