package compiler.ast;

public class VarLocation extends Location {
    public String name;

    public VarLocation(String name) {
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
