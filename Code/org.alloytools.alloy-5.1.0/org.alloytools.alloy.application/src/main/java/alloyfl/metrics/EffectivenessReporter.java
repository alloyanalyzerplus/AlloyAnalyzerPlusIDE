package alloyfl.metrics;

import static alloyfl.metrics.util.Util.unifyRankedNodesAndFaultyNodes;
import static parser.etc.Context.logger;
import static parser.util.Util.printEffectivenessReporterUsage;

import alloyfl.metrics.reporter.NearestNeighborDownwardReporter;
import alloyfl.metrics.reporter.NearestNeighborUpwardDownwardReporter;
import alloyfl.metrics.reporter.TopKExactMatchReporter;
import alloyfl.metrics.reporter.TopKSubMatchReporter;
import alloyfl.metrics.reporter.WorstCaseDownwardReporter;
import alloyfl.metrics.util.UnifiedNodeInfo;
import parser.util.FileUtil;

public class EffectivenessReporter {

  public static void main(String[] args) {
//    args = new String[]{
//        "experiments/results/real/cd3/cd3.flt",
//        "experiments/results/hybridfl/real/cd3/ochiai/average/100/cd3.lst"};
    if (args.length != 2) {
      logger.error("Wrong number of arguments: " + args.length);
      printEffectivenessReporterUsage();
      return;
    }
    String faultLocationFilePath = args[0];
    String faultRankingFilePath = args[1];
    if (!FileUtil.fileExists(faultLocationFilePath)) {
      logger.error("Cannot find fault location file at " + faultLocationFilePath);
      printEffectivenessReporterUsage();
      return;
    }
    if (!FileUtil.fileExists(faultRankingFilePath)) {
      logger.error("Cannot find fault list file at " + faultRankingFilePath);
      printEffectivenessReporterUsage();
      return;
    }
    UnifiedNodeInfo nodeInfo = unifyRankedNodesAndFaultyNodes(faultLocationFilePath,
        faultRankingFilePath);
    // Distance Metrics.
    logger.info(new NearestNeighborUpwardDownwardReporter().report(nodeInfo, 1).toString());
    logger.info(new NearestNeighborUpwardDownwardReporter().report(nodeInfo, 5).toString());
    logger.info(new NearestNeighborUpwardDownwardReporter().report(nodeInfo, 10).toString());
    logger.info(new NearestNeighborDownwardReporter().report(nodeInfo).toString());
    logger.info(new WorstCaseDownwardReporter().report(nodeInfo).toString());
    // Count Metrics.
    logger.info(new TopKExactMatchReporter().report(nodeInfo, 1).toString());
    logger.info(new TopKExactMatchReporter().report(nodeInfo, 5).toString());
    logger.info(new TopKExactMatchReporter().report(nodeInfo, 10).toString());
    logger.info(new TopKSubMatchReporter().report(nodeInfo, 1).toString());
    logger.info(new TopKSubMatchReporter().report(nodeInfo, 5).toString());
    logger.info(new TopKSubMatchReporter().report(nodeInfo, 10).toString());
  }
}
