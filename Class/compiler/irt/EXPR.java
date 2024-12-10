package compiler.irt;

public class EXPR extends IRStmt {
    public IRExp exp;

    public EXPR(IRExp exp) {
        this.exp = exp;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        return "EXPR " + exp.toString();
    }
}
