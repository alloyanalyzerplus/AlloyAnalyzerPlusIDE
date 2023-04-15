package mualloy.util;

public class AUnitTestCase {

    String testName;
    String body;
    String body_val_cmd;
    String valuation;
    String cmd;
    int    scope;
    int    expect;

    public AUnitTestCase(String testName, String body, int scope, int expect) {
        this.testName = testName;
        this.body = body;
        this.scope = scope;
        this.expect = expect;
    }

    public AUnitTestCase(String testName, String body, String body_val_cmd, int scope, int expect, String cmd, String valuation) {
        this.testName = testName;
        this.body = body;
        this.body_val_cmd = body_val_cmd;
        this.scope = scope;
        this.expect = expect;
        this.cmd = cmd;
        this.valuation = valuation;
    }

    public String toString(int num) {
        String name = testName + num;
        return "pred " + name + " {\n" + body + "\n}\nrun " + name + " for " + scope + " expect " + expect;
    }

    public String getAUnitFormat(int num) {
        return "val mutantVal" + num + " {\n" + body_val_cmd + "\n}\n@Test mutantTest" + num + ": run mutantVal" + num + " for " + scope + " expect " + expect;
    }

    public String getBody() {
        return body;
    }

    public String getCommand() {
        return cmd;
    }

    public String getValuation() {
        return valuation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AUnitTestCase)) {
            return false;
        }
        AUnitTestCase testCase = (AUnitTestCase) obj;
        return body.equals(testCase.body);
    }

    @Override
    public int hashCode() {
        return body.hashCode();
    }
}
