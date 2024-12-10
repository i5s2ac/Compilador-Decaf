package compiler.irt;

public class CONST extends IRExp {
    public int value;

    public CONST(int value) {
        this.value = value;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        return "CONST " + value;
    }
}
