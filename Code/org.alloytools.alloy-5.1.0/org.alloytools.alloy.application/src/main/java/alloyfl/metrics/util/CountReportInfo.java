package alloyfl.metrics.util;

public class CountReportInfo extends ReportInfo {

  private int topK;
  private int numOfFaultyNodes;

  public CountReportInfo(String header, boolean foundFault, int topK, int numOfFaultyNodes,
      int numOfTotalNodes) {
    super(header, foundFault, numOfTotalNodes);
    this.topK = topK;
    this.numOfFaultyNodes = numOfFaultyNodes;
  }

  @Override
  public String toString() {
    return header + ", FoundFault: " + foundFault
        + ", NumOfFaultyNodesInTop " + topK + ": " + numOfFaultyNodes
        + ", NumOfTotalNodes: " + numOfTotalNodes;
  }
}
