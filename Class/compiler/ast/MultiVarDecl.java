package compiler.ast;

import java.util.List;

public class MultiVarDecl implements ClassBodyMember {
    private final List<ClassBodyMember> declarations;

    public MultiVarDecl(List<ClassBodyMember> declarations) {
        this.declarations = declarations;
    }

    public List<ClassBodyMember> getDeclarations() {
        return declarations;
    }

    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiVarDecl{declarations=[");
        for (ClassBodyMember decl : declarations) {
            sb.append(decl.toString()).append(", ");
        }
        if (!declarations.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove last comma and space
        }
        sb.append("]}");
        return sb.toString();
    }
}
