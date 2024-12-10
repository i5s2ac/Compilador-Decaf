package compiler.ast;

public class ForStmt extends Statement {
    private Statement init;
    private Expression condition;
    private Statement update;
    private Statement body;

    public ForStmt(Statement init, Expression condition, Statement update, Statement body) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public Statement getInit() { return init; }
    public Expression getCondition() { return condition; }
    public Statement getUpdate() { return update; }
    public Statement getBody() { return body; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
