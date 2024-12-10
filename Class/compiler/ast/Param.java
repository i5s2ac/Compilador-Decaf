package compiler.ast;

public class Param implements AST {
    public Type type;
    public String name;
    public boolean isArray;

    public Param(Type type, String name, boolean isArray) {
        if (isArray) {
            this.type = new ArrayType(type);
        } else {
            this.type = type;
        }
        this.name = name;
        this.isArray = isArray;
    }

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}
