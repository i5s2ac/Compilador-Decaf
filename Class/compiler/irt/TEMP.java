package compiler.irt;

public class TEMP extends IRExp {
    public String name;

    public TEMP(String name) {
        this.name = name;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        return "TEMP " + name;
    }
}
