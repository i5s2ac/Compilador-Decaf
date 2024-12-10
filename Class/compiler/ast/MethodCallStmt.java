package compiler.ast;

public class MethodCallStmt extends Statement {
    private MethodCall methodCall;

    public MethodCallStmt(MethodCall methodCall) {
        this.methodCall = methodCall;
    }

    public MethodCall getMethodCall() {
        return methodCall;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
