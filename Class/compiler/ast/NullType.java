package compiler.ast;

/**
 * Representa el tipo null en el lenguaje.
 */
public class NullType extends Type {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullType;
    }

    @Override
    public String toString() {
        return "null";
    }

   @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
