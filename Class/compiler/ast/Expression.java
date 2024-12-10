package compiler.ast;

public abstract class Expression {
    public abstract void accept(ASTVisitor visitor);
}
