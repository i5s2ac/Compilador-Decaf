package compiler.irt;

public class RETURN extends IRStmt {

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }
    @Override
    public String toString() {
        return "RETURN";
    }
}
