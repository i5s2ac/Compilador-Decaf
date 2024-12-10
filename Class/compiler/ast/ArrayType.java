package compiler.ast;

public class ArrayType extends Type {
    private Type elementType;

    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    public Type getElementType() {
        return elementType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayType)) return false;
        ArrayType other = (ArrayType) obj;
        return this.elementType.equals(other.elementType);
    }

    @Override
    public String toString() {
        return elementType.toString() + "[]";
    }
}
