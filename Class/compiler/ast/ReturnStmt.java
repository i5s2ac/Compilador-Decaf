// En compiler/ast/ReturnStmt.java
package compiler.ast;

public class ReturnStmt extends Statement {
    private Expression expression;

    public ReturnStmt(Expression expression) {
        this.expression = expression;
    }

    // Método getter
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
