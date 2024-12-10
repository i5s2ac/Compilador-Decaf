package compiler.ast;

public class NewArrayExpr extends Expression {
    private Type elementType;
    private Expression size;
    private Type type; // Tipo de la expresión (ArrayType)

    public NewArrayExpr(Type elementType, Expression size) {
        this.elementType = elementType;
        this.size = size;
        this.type = new ArrayType(elementType); // Establecer el tipo de la expresión
    }

    // Métodos getter
    public Type getElementType() {
        return elementType;
    }


    public Expression getSize() {
        return size;
    }

    // Elimina la anotación @Override
    public Type getType() {
        return type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
