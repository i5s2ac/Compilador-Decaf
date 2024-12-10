package compiler.ast;

import java.util.List;
import java.util.ArrayList;

public class CalloutCall extends MethodCall {
    private String functionName;
    private List<CalloutArg> calloutArguments;

    public CalloutCall(String functionName, List<CalloutArg> calloutArguments) {
        super(functionName, new ArrayList<Expression>(calloutArguments));
        this.functionName = functionName;
        this.calloutArguments = calloutArguments;
    }

    // Cambiar el nombre del método a getName() para que coincida con el SemanticAnalyzer
    public String getName() {
        return functionName;
    }

    // Método original si aún lo necesitas en otra parte del código
    public String getFunctionName() {
        return functionName;
    }

    // Método para obtener los argumentos
    public List<CalloutArg> getArgs() {
        return calloutArguments;
    }

    // Método original si aún lo necesitas en otra parte del código
    public List<CalloutArg> getCalloutArguments() {
        return calloutArguments;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}