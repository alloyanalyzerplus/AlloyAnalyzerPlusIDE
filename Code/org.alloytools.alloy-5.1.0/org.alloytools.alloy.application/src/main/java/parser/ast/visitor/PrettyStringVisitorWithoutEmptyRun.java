package parser.ast.visitor;

import static parser.etc.Names.COMMA;
import static parser.etc.Names.DOLLAR;
import static parser.etc.Names.UNDERSCORE;

import java.util.List;
import java.util.stream.Collectors;

import parser.ast.nodes.ModuleDecl;
import parser.ast.nodes.OpenDecl;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.Run;

public class PrettyStringVisitorWithoutEmptyRun extends PrettyStringVisitor {

    @Override
    public String visit(OpenDecl n, Object arg) {
        return "";
    }


    @Override
    public String visit(ModuleDecl n, Object arg) {
        return "";
    }

    @Override
    public String visit(Predicate n, Object arg) {
        if (n.getName().replaceAll("\\" + DOLLAR, UNDERSCORE).startsWith("run_"))
            return putInMap(n, "");
        List<ParamDecl> paramDeclList = n.getParamList();
        if (!paramDeclList.isEmpty()) {
            ParamDecl paramDecl = paramDeclList.get(0);
            if (paramDecl.getNames().size() == 1 && paramDecl.getNames().get(0).equals("this")) {
                return putInMap(n, "pred " + paramDecl.getExpr().accept(this, arg) + "." + n.getName().replaceAll("\\" + DOLLAR, UNDERSCORE) + "[" + String.join(COMMA, paramDeclList.subList(1, paramDeclList.size()).stream().map(parameter -> parameter.accept(this, arg)).collect(Collectors.toList())) + "] " + n.getBody().accept(this, arg));
            }
        }
        return putInMap(n, "pred " + n.getName().replaceAll("\\" + DOLLAR, UNDERSCORE) + "[" + String.join(COMMA, n.getParamList().stream().map(parameter -> parameter.accept(this, arg)).collect(Collectors.toList())) + "] " + n.getBody().accept(this, arg));
    }

    @Override
    public String visit(Run n, Object arg) {
        if (n.getName().replaceAll("\\" + DOLLAR, UNDERSCORE).startsWith("run_"))
            return "";
        return putInMap(n, "run " + n.getName().replaceAll("\\" + DOLLAR, UNDERSCORE) + n.getScopeAsString());
    }
}
