package compiler.ast;

import java.util.List;

public class MethodCall extends Expression {
    protected String methodName;
    protected List<Expression> arguments;

    public MethodCall(String methodName, List<Expression> arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    // Getter para methodName
    public String getMethodName() {
        return methodName;
    }

    // Getter para arguments
    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
