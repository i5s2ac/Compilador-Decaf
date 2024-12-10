package compiler.irt;

public class LABEL extends IRStmt {
    public String label;

    public LABEL(String label) {
        this.label = label;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        return "LABEL " + label;
    }
}
