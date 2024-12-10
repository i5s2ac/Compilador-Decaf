package compiler.irt;

public class IRTPrinter implements IRTVisitor {
    @Override
    public void visit(BinOpExpr node, String prefix) {
        System.out.println(prefix + "└── BinOp: " + node.op);
        node.left.accept(this, prefix + "    ├── ");
        node.right.accept(this, prefix + "    └── ");
    }

    @Override
    public void visit(CALL node, String prefix) {
        System.out.println(prefix + "└── CALL");
        node.func.accept(this, prefix + "    ├── ");
        for (int i = 0; i < node.args.size(); i++) {
            boolean isLast = i == node.args.size() - 1;
            node.args.get(i).accept(this, prefix + "    " + (isLast ? "└── " : "├── "));
        }
    }

    @Override
    public void visit(CONST node, String prefix) {
        System.out.println(prefix + "└── CONST: " + node.value);
    }

    @Override
    public void visit(MEM node, String prefix) {
        System.out.println(prefix + "└── MEM");
        node.exp.accept(this, prefix + "    └── ");
    }

    @Override
    public void visit(NAME node, String prefix) {
        System.out.println(prefix + "└── NAME: " + node.label);
    }

    @Override
    public void visit(TEMP node, String prefix) {
        System.out.println(prefix + "└── TEMP: " + node.name);
    }

    @Override
    public void visit(MOVE node, String prefix) {
        System.out.println(prefix + "└── MOVE");
        System.out.println(prefix + "    ├── Destino:");
        node.dst.accept(this, prefix + "    │   ");
        System.out.println(prefix + "    └── Fuente:");
        node.src.accept(this, prefix + "        ");
    }

    @Override
    public void visit(JUMP node, String prefix) {
        System.out.println(prefix + "└── JUMP");
        node.exp.accept(this, prefix + "    └── ");
    }

    @Override
    public void visit(CJUMP node, String prefix) {
        System.out.println(prefix + "└── CJUMP (" + node.relop + ")");
        System.out.println(prefix + "    ├── Condición:");
        node.left.accept(this, prefix + "    │   ");
        node.right.accept(this, prefix + "    │   ");
        System.out.println(prefix + "    ├── True: " + node.trueLabel);
        System.out.println(prefix + "    └── False: " + node.falseLabel);
    }

    @Override
    public void visit(SEQ node, String prefix) {
        System.out.println(prefix + "└── SEQ");
        for (int i = 0; i < node.stmts.size(); i++) {
            boolean isLast = i == node.stmts.size() - 1;
            node.stmts.get(i).accept(this, prefix + "    " + (isLast ? "└── " : "├── "));
        }
    }

    @Override
    public void visit(LABEL node, String prefix) {
        System.out.println(prefix + "└── LABEL: " + node.label);
    }

    @Override
    public void visit(EXPR node, String prefix) {
        System.out.println(prefix + "└── EXPR");
        node.exp.accept(this, prefix + "    └── ");
    }

    @Override
    public void visit(RETURN node, String prefix) {
        System.out.println(prefix + "└── RETURN");
    }
}