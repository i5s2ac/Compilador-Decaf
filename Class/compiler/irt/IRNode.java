package compiler.irt;

public abstract class IRNode {
    public abstract void accept(IRTVisitor visitor, String prefix);
    
    protected String getIndent(String prefix) {
        return prefix + "  ";
    }
}