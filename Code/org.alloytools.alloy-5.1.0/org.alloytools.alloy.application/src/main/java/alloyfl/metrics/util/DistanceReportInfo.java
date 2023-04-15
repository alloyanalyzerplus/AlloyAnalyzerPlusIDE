package alloyfl.metrics.util;

public class DistanceReportInfo extends ReportInfo {

  private Integer topK;
  private final int numOfExploredNodes;

  public DistanceReportInfo(String header, boolean foundFault, int numOfExploredNodes,
      int numOfTotalNodes) {
    this(header, foundFault, null, numOfExploredNodes,numOfTotalNodes);
  }

  public DistanceReportInfo(String header, boolean foundFault, Integer topK, int numOfExploredNodes,
      int numOfTotalNodes) {
    super(header, foundFault, numOfTotalNodes);
    this.topK = topK;
    this.numOfExploredNodes = numOfExploredNodes;
  }

  @Override
  public String toString() {
    return header + ", FoundFault: " + foundFault
        + (topK == null ? "" : (", StartFromTop " + topK))
        + ", NumOfExploredNodes: " + numOfExploredNodes
        + ", NumOfTotalNodes: " + numOfTotalNodes;
  }
}
