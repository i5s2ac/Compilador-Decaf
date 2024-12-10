package compiler.ast;

public class BoolLiteral extends Literal {
    private boolean value;
    private Type type;

    public BoolLiteral(boolean value) {
        this.value = value;
        this.type = new BooleanType();
    }

    public boolean getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
