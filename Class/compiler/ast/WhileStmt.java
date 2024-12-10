// En compiler/ast/WhileStmt.java
package compiler.ast;

public class WhileStmt extends Statement {
    private Expression condition;
    private Block body;

    public WhileStmt(Expression condition, Block body) {
        this.condition = condition;
        this.body = body;
    }

    // Métodos getter
    public Expression getCondition() {
        return condition;
    }

    public Block getBody() {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
