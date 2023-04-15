package alloyfl.mutation.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import parser.ast.nodes.Node;
import parser.ast.visitor.PrettyStringVisitor;
import parser.util.AlloyUtil;

public class ScoreInfo {

  private Node node;
  private double score;
  private int descNum;
  private PrettyStringVisitor psv;
  private NumberFormat formatter;

  public ScoreInfo(Node node, double score, int descNum,
      PrettyStringVisitor psv) {
    this.node = node;
    this.score = score;
    this.descNum = descNum;
    this.psv = psv;
    this.formatter = new DecimalFormat("#0.00");
  }

  public Node getNode() {
    return node;
  }

  public String getNodeAsString() {
    return AlloyUtil.computeNodePathAsString(node, psv);
  }

  public double getScore() {
    return score;
  }

  public String getScoreAsString() {
    return formatter.format(score);
  }

  public int getDescNum() {
    return descNum;
  }

  @Override
  public String toString() {
    return "<" + getNodeAsString() + ": " + getScoreAsString() + ">";
  }

  public static List<ScoreInfo> sortScoreInfos(List<ScoreInfo> scoreInfos) {
    scoreInfos.sort((scoreInfo1, scoreInfo2) -> {
      // Descending order of the score.
      int cmpRes = Double.compare(scoreInfo2.getScore(), scoreInfo1.getScore());
      if (cmpRes != 0) {
        return cmpRes;
      }
      // Ascending order of the descendant number.
      return Integer.compare(scoreInfo1.getDescNum(), scoreInfo2.getDescNum());
    });
    return scoreInfos;
  }
}
