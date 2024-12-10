package compiler.irt;

public class MEM extends IRExp {
    public IRExp exp;

    public MEM(IRExp exp) {
        this.exp = exp;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        return "MEM[" + exp.toString() + "]";
    }
}
