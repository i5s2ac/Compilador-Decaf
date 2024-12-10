package compiler.ast;

public class CalloutStmt extends Statement {
    private CalloutCall calloutCall;

    public CalloutStmt(CalloutCall calloutCall) {
        this.calloutCall = calloutCall;
    }

    public CalloutCall getCalloutCall() {
        return calloutCall;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
