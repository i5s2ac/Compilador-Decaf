package compiler.ast;

public class BinaryExpr extends Expression {
    public Expression left;
    public String op;
    public Expression right;

    public BinaryExpr(Expression left, String op, Expression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
