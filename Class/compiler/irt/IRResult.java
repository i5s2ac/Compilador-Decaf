package compiler.irt;

import java.util.*;

public class IRResult {
    public IRStmt ir; // Cambiado de IRExp a IRStmt
    public ControlFlowGraph cfg;
    public List<String> errors;

    public IRResult(IRStmt ir, ControlFlowGraph cfg) {
        this.ir = ir;
        this.cfg = cfg;
        this.errors = new ArrayList<>();
    }
}
