package compiler.irt;

public class NAME extends IRExp {
    public String label;

    public NAME(String label) {
        this.label = label;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }


    @Override
    public String toString() {
        return "NAME " + label;
    }
}
