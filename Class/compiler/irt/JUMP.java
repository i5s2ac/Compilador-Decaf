package compiler.irt;

public class JUMP extends IRStmt {
    public IRExp exp;

    public JUMP(IRExp exp) {
        this.exp = exp;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }


    @Override
    public String toString() {
        return "JUMP " + exp.toString();
    }
}
