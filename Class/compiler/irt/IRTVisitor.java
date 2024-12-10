package compiler.irt;

public interface IRTVisitor {
    void visit(BinOpExpr node, String prefix);
    void visit(CALL node, String prefix);
    void visit(CONST node, String prefix);
    void visit(MEM node, String prefix);
    void visit(NAME node, String prefix);
    void visit(TEMP node, String prefix);
    void visit(MOVE node, String prefix);
    void visit(JUMP node, String prefix);
    void visit(CJUMP node, String prefix);
    void visit(SEQ node, String prefix);
    void visit(LABEL node, String prefix);
    void visit(EXPR node, String prefix);
    void visit(RETURN node, String prefix);
}