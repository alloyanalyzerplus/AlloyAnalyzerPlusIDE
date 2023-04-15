package alloyfl.coverage.util;

import java.util.Collection;
import parser.ast.nodes.Node;

public class TestResult {
  /** Expect test to be satisfiable. */
  private boolean expect;
  /** Whether the test is actually satisfiable. */
  private boolean actual;
  private Collection<Node> relevantNodes;
  private Collection<Node> unsatNodes;

  public TestResult(boolean expect, boolean actual, Collection<Node> relevantNodes) {
    this.expect = expect;
    this.actual = actual;
    this.relevantNodes = relevantNodes;
    this.unsatNodes = null;
  }

  public boolean isFailed() {
    return expect != actual;
  }

  public Collection<Node> getRelevantNodes() {
    return relevantNodes;
  }

  public void setRelevantNodes(Collection<Node> relevantNodes) {
    this.relevantNodes = relevantNodes;
  }

  public Collection<Node> getUnsatNodes() {
    return unsatNodes;
  }

  public void setUnsatNodes(Collection<Node> unsatNodes) {
    this.unsatNodes = unsatNodes;
  }
}
