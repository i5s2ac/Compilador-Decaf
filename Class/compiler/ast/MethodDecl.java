package compiler.ast;

import java.util.List;

public class MethodDecl implements ClassBodyMember {
    public Type returnType;
    public String name;
    public List<Param> params;
    public Block body;

    public MethodDecl(Type returnType, String name, List<Param> params, Block body) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
