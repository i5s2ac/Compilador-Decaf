package compiler.ast;

public class UnaryExpr extends Expression {
    public String op;
    public Expression expr;

    public UnaryExpr(String op, Expression expr) {
        this.op = op;
        this.expr = expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
