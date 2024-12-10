// En compiler/ast/BreakStmt.java
package compiler.ast;

public class BreakStmt extends Statement {
    public BreakStmt() {
        // Constructor vacío
    }

@Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
    
}
