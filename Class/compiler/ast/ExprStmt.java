package compiler.ast;

public class ExprStmt extends Statement {
    private Expression expression;

    public ExprStmt(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
