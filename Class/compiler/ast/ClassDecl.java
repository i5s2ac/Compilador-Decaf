package compiler.ast;

import java.util.List;

public class ClassDecl implements ClassBodyMember {
    public String name;
    public List<ClassBodyMember> body;

    public ClassDecl(String name, List<ClassBodyMember> body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
