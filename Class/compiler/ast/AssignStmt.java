package compiler.ast;

public class AssignStmt extends Statement {
    public Location location;
    public String op;
    public Expression expr;

    public AssignStmt(Location location, String op, Expression expr) {
        this.location = location;
        this.op = op;
        this.expr = expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
