package parser.etc;

import java.util.LinkedHashMap;
import java.util.Map;
import parser.ast.nodes.Node;

public class NodeMap {

  /**
   * Maps each AST node in Alloy to the corresponding node in AParser.
   */
  private Map<Object, Node> s2d;
  /**
   * Maps each AST node in AParser to the corresponding node in Alloy.
   */
  private Map<Node, Object> d2s;

  public NodeMap() {
    this.s2d = new LinkedHashMap<>();
    this.d2s = new LinkedHashMap<>();
  }

  public void addToMap(Object src, Node dst) {
    s2d.put(src, dst);
    d2s.put(dst, src);
  }

  public Map<Object, Node> getS2D() {
    return s2d;
  }

  public Map<Node, Object> getD2S() {
    return d2s;
  }

  public Node findDst(Object src) {
    return s2d.get(src);
  }

  public Object findSrc(Node dst) {
    return d2s.get(dst);
  }
}
