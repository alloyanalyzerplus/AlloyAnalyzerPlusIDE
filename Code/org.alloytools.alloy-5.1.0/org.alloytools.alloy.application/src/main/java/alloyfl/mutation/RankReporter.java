package alloyfl.mutation;

import static parser.etc.Context.logger;
import static parser.util.Util.printRankReporterUsage;

import java.util.List;
import parser.util.FileUtil;
import parser.util.StringUtil;

public class RankReporter {

  /**
   * Compute the rank of the fault given a fault ranking list. The rank starts at 1.
   */
  public static String computeCorrectFaultRanking(String faultLocFilePath,
      String faultRankingFilePath) {
    String faultAsString = FileUtil.readText(faultLocFilePath).trim();
    List<String> nodeRanking = StringUtil.decode(FileUtil.readText(faultRankingFilePath).trim());
    int total = nodeRanking.size() / 2;
    RankInfo rankInfo = computeRankInfo(faultAsString, nodeRanking);
    return rankInfo.matchType + ", Rank: " + rankInfo.rank + ", Total: " + total + ", Score: "
        + rankInfo.score;
  }

//    public static RankInfo computeRankInfo(String faultAsString, List<String> nodeRanking, BiFunction<String,String,Boolean> matchFunction) {
//        int total = nodeRanking.size() / 2;
//        int rank = -1;
//        String lastScore = "";
//        int cnt = 0;
//        boolean match = false;
//        for (int i = 0; i < total; i++) {
//            String nodeAsString = nodeRanking.get(i * 2);
//            String score = nodeRanking.get(i * 2 + 1);
//            cnt++;
//            if (!lastScore.equals(score)) {
//                rank = cnt;
//                lastScore = score;
//            }
//            if (matchFunction.apply(nodeAsString, faultAsString)) {
//                match = true;
//                break;
//            }
//        }
//        return new RankInfo(match, rank, lastScore);
//    }

  public static RankInfo computeRankInfo(String faultAsString, List<String> nodeRanking) {
    int total = nodeRanking.size() / 2;
    int rank = 0;
    String lastScore = "";
    String matchType = "NoMatch";
    for (int i = 0; i < total; i++) {
      String nodeAsString = nodeRanking.get(i * 2);
      String score = nodeRanking.get(i * 2 + 1);
      if (!matchType.equals("NoMatch") && !lastScore.equals(score)) {
        break;
      }
      rank++;
      if (matchType.equals("NoMatch")) {
        if (nodeAsString.equals(faultAsString)) {
          matchType = "ExactMatch";
        } else if (nodeAsString.contains(faultAsString)) {
          matchType = "SubMatch";
        } else if (faultAsString.contains(nodeAsString)) {
          matchType = "SuperMatch";
        }
        lastScore = score;
      } else if (!matchType.equals("ExactMatch")) { // Prefer ExactMatch in case of tie.
        if (nodeAsString.equals(faultAsString)) {
          matchType = "ExactMatch";
        }
      }
    }
    return new RankInfo(matchType, String.valueOf(rank), lastScore);
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      logger.error("Wrong number of arguments: " + args.length);
      printRankReporterUsage();
      return;
    }
    String faultLocFilePath = args[0];
    String faultRankingFilePath = args[1];
    if (!FileUtil.fileExists(faultLocFilePath)) {
      logger.error("Cannot find fault location file at " + faultLocFilePath);
      printRankReporterUsage();
      return;
    }
    if (!FileUtil.fileExists(faultRankingFilePath)) {
      logger.error("Cannot find fault ranking file at " + faultRankingFilePath);
      printRankReporterUsage();
      return;
    }
    System.out.println(computeCorrectFaultRanking(faultLocFilePath, faultRankingFilePath));
  }

  static class RankInfo {

    String matchType;
    String rank;
    String score;

    RankInfo(String matchType, String rank, String score) {
      this.matchType = matchType;
      if (matchType.equals("NoMatch")) {
        this.rank = "unknown";
        this.score = "unknown";
        return;
      }
      this.rank = rank;
      this.score = score;
    }
  }
}
