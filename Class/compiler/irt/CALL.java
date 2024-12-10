package compiler.irt;

import java.util.List;

public class CALL extends IRExp {
    public IRExp func;
    public List<IRExp> args;

    public CALL(IRExp func, List<IRExp> args) {
        this.func = func;
        this.args = args;
    }

    @Override
    public void accept(IRTVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CALL ").append(func.toString()).append("(");
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i).toString());
            if (i < args.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
