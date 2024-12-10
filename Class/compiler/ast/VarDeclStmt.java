package compiler.ast;

public class VarDeclStmt extends Statement {
    private VarDecl varDecl;
    private Expression initExpression;

    public VarDeclStmt(VarDecl varDecl, Expression initExpression) {
        this.varDecl = varDecl;
        this.initExpression = initExpression;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public Expression getInitExpression() {
        return initExpression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
