package compiler.ast;

public class FieldDecl implements ClassBodyMember {
    public Type type;
    public String name;
    public boolean isArray;
    public Expression initExpr; // Agregar este campo

    public FieldDecl(Type type, String name, boolean isArray, Expression initExpr) {
        if (isArray) {
            this.type = new ArrayType(type);
        } else {
            this.type = type;
        }
        this.name = name;
        this.isArray = isArray;
        this.initExpr = initExpr; // Inicializar el campo
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
