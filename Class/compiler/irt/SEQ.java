package compiler.irt;

import java.util.List;

public class SEQ extends IRStmt {
    public List<IRStmt> stmts;

    public SEQ(List<IRStmt> stmts) {
        this.stmts = stmts;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRStmt stmt : stmts) {
            sb.append(stmt.toString()).append("\n");
        }
        return sb.toString();
    }
}
