package compiler.ast;

public class AssignExpr extends Expression {
    private Location location;
    private String operator; 
    private Expression expression;

    public AssignExpr(Location location, String operator, Expression expression) {
        this.location = location;
        this.operator = operator;
        this.expression = expression;
    }

    public Location getLocation() {
        return location;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
