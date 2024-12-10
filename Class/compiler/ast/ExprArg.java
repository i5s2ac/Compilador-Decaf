package compiler.ast;

public class ExprArg extends CalloutArg {
    private Expression expression;

    public ExprArg(Expression expression) {
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
