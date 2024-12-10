package compiler.ast;

public interface ClassBodyMember {
    void accept(ASTVisitor visitor);
}
