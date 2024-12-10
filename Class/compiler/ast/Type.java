package compiler.ast;

public abstract class Type implements AST {
    @Override
    public abstract void accept(ASTVisitor visitor);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();
}
