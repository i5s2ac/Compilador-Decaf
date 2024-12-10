// En compiler/ast/IfStmt.java
package compiler.ast;

public class IfStmt extends Statement {
    private Expression condition;
    private Block thenBlock;
    private Block elseBlock;

    public IfStmt(Expression condition, Block thenBlock, Block elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    // Métodos getter
    public Expression getCondition() {
        return condition;
    }

    public Block getThenBlock() {
        return thenBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
