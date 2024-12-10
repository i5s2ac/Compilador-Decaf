package compiler.irt;

import java.util.ArrayList;
import java.util.List;

public class ControlFlowGraph {
    public List<BasicBlock> blocks;

    public ControlFlowGraph() {
        this.blocks = new ArrayList<>();
    }
}
