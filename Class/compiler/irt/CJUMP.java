package compiler.irt;

public class CJUMP extends IRStmt {
    public BinOp relop;
    public IRExp left, right;
    public String trueLabel, falseLabel;

    public CJUMP(BinOp relop, IRExp left, IRExp right, String trueLabel, String falseLabel) {
        this.relop = relop;
        this.left = left;
        this.right = right;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        return "CJUMP(" + relop.toString() + ", " + left.toString() + ", " + right.toString() +
               ", " + trueLabel + ", " + falseLabel + ")";
    }
}
