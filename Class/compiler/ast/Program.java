package compiler.ast;

import java.util.List;

public class Program {
    public String className;
    public List<ClassBodyMember> classBody;

    public Program(String className, List<ClassBodyMember> classBody) {
        this.className = className;
        this.classBody = classBody;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
