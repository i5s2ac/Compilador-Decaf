package compiler.ast;

import java.util.List;

public class Block extends Statement {
    public List<VarDecl> varDecls;
    public List<Statement> statements;

    public Block(List<VarDecl> varDecls, List<Statement> statements) {
        this.varDecls = varDecls;
        this.statements = statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
