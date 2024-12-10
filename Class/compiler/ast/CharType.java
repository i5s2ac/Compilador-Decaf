package compiler.ast;

public class CharType extends Type {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CharType;
    }

    @Override
    public String toString() {
        return "char";
    }
}
