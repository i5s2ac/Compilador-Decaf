package compiler.ast;

public class ArrayLocation extends Location {
    public String name;
    public Expression index;

    public ArrayLocation(String name, Expression index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
