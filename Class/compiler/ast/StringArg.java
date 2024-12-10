package compiler.ast;

public class StringArg extends CalloutArg {
    private String value;

    public StringArg(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
