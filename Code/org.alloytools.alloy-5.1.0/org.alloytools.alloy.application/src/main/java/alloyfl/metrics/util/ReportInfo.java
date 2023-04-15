package alloyfl.metrics.util;

public abstract class ReportInfo {
  protected final String header;
  protected final boolean foundFault;
  protected final int numOfTotalNodes;

  public ReportInfo(String header, boolean foundFault, int numOfTotalNodes) {
    this.header = header;
    this.foundFault = foundFault;
    this.numOfTotalNodes = numOfTotalNodes;
  }
}
