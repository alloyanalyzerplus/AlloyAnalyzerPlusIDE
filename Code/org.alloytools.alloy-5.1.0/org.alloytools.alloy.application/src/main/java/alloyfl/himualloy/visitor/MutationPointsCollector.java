package alloyfl.himualloy.visitor;

import static parser.etc.Names.DOLLAR;
import static parser.etc.Names.IGNORE_NAMES;

import java.util.ArrayList;
import java.util.List;
import mualloy.util.MInfo;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExpr;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ListExpr;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.VoidVisitorAdapter;

public class MutationPointsCollector extends VoidVisitorAdapter<Object> {

  protected List<Node> mutationPoints;
  protected MInfo.MType state;

  public MutationPointsCollector() {
    this.mutationPoints = new ArrayList<>();
    this.state = MInfo.MType.UNKNOWN;
  }

  public List<Node> getMutationPoints() {
    return mutationPoints;
  }

  @Override
  public void visit(ModelUnit n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(SigDecl n, Object arg) {
    MInfo.MType oldState = state;
    state = MInfo.MType.SIG;
    // Potential MOR.
    mutationPoints.add(n);
    super.visit(n, arg);
    state = oldState;
  }

  @Override
  public void visit(FieldDecl n, Object arg) {
    MInfo.MType oldState = state;
    state = MInfo.MType.FIELD;
    super.visit(n, arg);
    state = oldState;
  }

  @Override
  public void visit(ParamDecl n, Object arg) {
    MInfo.MType oldState = state;
    state = MInfo.MType.IGNORE;
    super.visit(n, arg);
    state = oldState;
  }

  @Override
  public void visit(VarDecl n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(ExprOrFormula n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(FieldExpr n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential UOI.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(VarExpr n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential UOI.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryExpr n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential UOR, UOI and UOD.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryFormula n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential UOR and UOD.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryExpr n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential BOR, UOI and BOE.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryFormula n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential BOR and BOE.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ListExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(ListFormula n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential LOR.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(CallExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(CallFormula n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(QtExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(QtFormula n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential QOR.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEFormula n, Object arg) {
    if (state != MInfo.MType.IGNORE) {
      // Potential IEOE.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(LetExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(Body n, Object arg) {
    if (state != MInfo.MType.IGNORE && (n.getParent() instanceof Predicate || n
        .getParent() instanceof Fact)) {
      // Potential PBD.
      mutationPoints.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Predicate n, Object arg) {
    MInfo.MType oldState = state;
    if (n.getName().contains(DOLLAR) || IGNORE_NAMES.stream()
        .anyMatch(ignoredName -> n.getName().startsWith(ignoredName))) {
      state = MInfo.MType.IGNORE;
    } else {
      state = MInfo.MType.PRED;
    }
    super.visit(n, arg);
    state = oldState;
  }

  @Override
  public void visit(Function n, Object arg) {
    MInfo.MType oldState = state;
    if (n.getName().contains(DOLLAR) || IGNORE_NAMES.stream()
        .anyMatch(ignoredName -> n.getName().startsWith(ignoredName))) {
      state = MInfo.MType.IGNORE;
    } else {
      state = MInfo.MType.FUN;
    }
    super.visit(n, arg);
    state = oldState;
  }

  @Override
  public void visit(Fact n, Object arg) {
    MInfo.MType oldState = state;
    if (IGNORE_NAMES.stream()
        .anyMatch(ignoredName -> n.getName().startsWith(ignoredName))) {
      state = MInfo.MType.IGNORE;
    } else {
      state = MInfo.MType.FACT;
    }
    super.visit(n, arg);
    state = oldState;
  }

  @Override
  public void visit(Assertion n, Object arg) {
    MInfo.MType oldState = state;
    state = MInfo.MType.IGNORE;
    super.visit(n, arg);
    state = oldState;
  }
}
