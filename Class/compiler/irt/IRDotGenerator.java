package compiler.irt;

import java.io.PrintWriter;

public class IRDotGenerator implements IRTVisitor {
    private final PrintWriter writer;
    private int nodeId = 0;

    public IRDotGenerator(PrintWriter writer) {
        this.writer = writer;
    }

    public void beginGraph() {
        writer.println("digraph IR {");
        writer.println("  node [shape=box, fontname=\"Arial\"];");
    }

    public void endGraph() {
        writer.println("}");
    }

    private int newNodeId() {
        return nodeId++;
    }

    private void writeNode(int id, String label) {
        writer.printf("  node%d [label=\"%s\"];\n", id, label);
    }

    private void writeEdge(int fromId, int toId) {
        writer.printf("  node%d -> node%d;\n", fromId, toId);
    }

    @Override
    public void visit(BinOpExpr node, String prefix) {
        int currentId = newNodeId();
        writeNode(currentId, "BinOp: " + node.op);
        int leftId = newNodeId();
        writeNode(leftId, node.left.toString());
        writeEdge(currentId, leftId);

        int rightId = newNodeId();
        writeNode(rightId, node.right.toString());
        writeEdge(currentId, rightId);
    }

    @Override
    public void visit(CALL node, String prefix) {
        int currentId = newNodeId();
        writeNode(currentId, "CALL");
        int funcId = newNodeId();
        writeNode(funcId, node.func.toString());
        writeEdge(currentId, funcId);

        for (IRExp arg : node.args) {
            int argId = newNodeId();
            writeNode(argId, arg.toString());
            writeEdge(currentId, argId);
        }
    }

    @Override
    public void visit(CONST node, String prefix) {
        int id = newNodeId();
        writeNode(id, "CONST: " + node.value);
    }

    @Override
    public void visit(MOVE node, String prefix) {
        int currentId = newNodeId();
        writeNode(currentId, "MOVE");
        int dstId = newNodeId();
        writeNode(dstId, node.dst.toString());
        writeEdge(currentId, dstId);

        int srcId = newNodeId();
        writeNode(srcId, node.src.toString());
        writeEdge(currentId, srcId);
    }

    @Override
    public void visit(LABEL node, String prefix) {
        int id = newNodeId();
        writeNode(id, "LABEL: " + node.label);
    }

    @Override
    public void visit(JUMP node, String prefix) {
        int currentId = newNodeId();
        writeNode(currentId, "JUMP");
        int expId = newNodeId();
        writeNode(expId, node.exp.toString());
        writeEdge(currentId, expId);
    }

    @Override
    public void visit(SEQ node, String prefix) {
        int currentId = newNodeId();
        writeNode(currentId, "SEQ");

        for (IRStmt stmt : node.stmts) {
            int stmtId = newNodeId();
            stmt.accept(this, "");
            writeEdge(currentId, stmtId);
        }
    }

    @Override
    public void visit(EXPR node, String prefix) {
        int id = newNodeId();
        writeNode(id, "EXPR");
    }

    @Override
    public void visit(TEMP node, String prefix) {
        int id = newNodeId();
        writeNode(id, "TEMP: " + node.name);
    }

    @Override
    public void visit(RETURN node, String prefix) {
        int id = newNodeId();
        writeNode(id, "RETURN");
    }

    @Override
    public void visit(MEM node, String prefix) {
        int id = newNodeId();
        writeNode(id, "MEM");
    }

    @Override
    public void visit(NAME node, String prefix) {
        int id = newNodeId();
        writeNode(id, "NAME: " + node.label);
    }

    @Override
    public void visit(CJUMP node, String prefix) {
        int currentId = newNodeId();
        writeNode(currentId, "CJUMP (" + node.relop + ")");
        int leftId = newNodeId();
        writeNode(leftId, node.left.toString());
        writeEdge(currentId, leftId);

        int rightId = newNodeId();
        writeNode(rightId, node.right.toString());
        writeEdge(currentId, rightId);
    }
}
