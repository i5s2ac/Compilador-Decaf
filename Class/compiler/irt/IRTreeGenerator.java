package compiler.irt;

import compiler.ast.*;
import java.util.*;
import compiler.irt.*;
import compiler.semantic.*;

public class IRTreeGenerator implements ASTVisitor {
    private Map<String, Integer> tempCounter;
    private Map<String, Integer> labelCounter;
    private Stack<String> currentLoop;
    private String currentMethod;
    private boolean inMethod;
    private List<IRStmt> irStatements;

    public IRTreeGenerator() {
        this.tempCounter = new HashMap<>();
        this.labelCounter = new HashMap<>();
        this.currentLoop = new Stack<>();
        this.currentMethod = null;
        this.inMethod = false;
        this.irStatements = new ArrayList<>();
    }

    private String newTemp() {
        int count = tempCounter.getOrDefault(currentMethod, 0);
        tempCounter.put(currentMethod, count + 1);
        return "t_" + currentMethod + "_" + count;
    }

    private String newLabel(String prefix) {
        int count = labelCounter.getOrDefault(prefix, 0);
        labelCounter.put(prefix, count + 1);
        return prefix + "_" + count;
    }

    public List<IRStmt> getIRStatements() {
        return irStatements;
    }

    public IRResult generateAndOptimize(Program program) {
        // Reiniciar el estado interno
        this.irStatements = new ArrayList<>();
        this.tempCounter = new HashMap<>();
        this.labelCounter = new HashMap<>();
        this.currentLoop = new Stack<>();
        this.currentMethod = null;
        this.inMethod = false;

        // Generar el árbol IR
        program.accept(this);

        IRStmt ir = new SEQ(irStatements);

        // Construir el CFG a partir de las instrucciones IR
        ControlFlowGraph cfg = buildCFG(irStatements);

        // Crear el resultado de IR
        IRResult result = new IRResult(ir, cfg);

        return result;
    }

    private ControlFlowGraph buildCFG(List<IRStmt> statements) {
        ControlFlowGraph cfg = new ControlFlowGraph();
        Map<String, BasicBlock> labelToBlock = new HashMap<>();
        BasicBlock currentBlock = new BasicBlock();
        cfg.blocks.add(currentBlock);

        for (IRStmt stmt : statements) {
            if (stmt instanceof LABEL) {
                String label = ((LABEL) stmt).label;
                if (!currentBlock.instructions.isEmpty()) {
                    currentBlock = new BasicBlock();
                    cfg.blocks.add(currentBlock);
                }
                currentBlock.label = label;
                labelToBlock.put(label, currentBlock);
            }

            currentBlock.addInstruction(stmt);

            if (stmt instanceof JUMP) {
                IRExp target = ((JUMP) stmt).exp;
                if (target instanceof NAME) {
                    String label = ((NAME) target).label;
                    currentBlock.successorLabels.add(label);
                }
                currentBlock = new BasicBlock();
                cfg.blocks.add(currentBlock);
            } else if (stmt instanceof CJUMP) {
                CJUMP cjump = (CJUMP) stmt;
                currentBlock.successorLabels.add(cjump.trueLabel);
                currentBlock.successorLabels.add(cjump.falseLabel);
                currentBlock = new BasicBlock();
                cfg.blocks.add(currentBlock);
            } else if (stmt instanceof RETURN) {
                currentBlock = new BasicBlock();
                cfg.blocks.add(currentBlock);
            }
        }

        // Resolver los sucesores y predecesores
        for (BasicBlock block : cfg.blocks) {
            for (String succLabel : block.successorLabels) {
                BasicBlock succBlock = labelToBlock.get(succLabel);
                if (succBlock != null) {
                    block.successors.add(succBlock);
                    succBlock.predecessors.add(block);
                }
            }
        }

        return cfg;
    }

    @Override
    public void visit(Program program) {
        for (ClassBodyMember member : program.classBody) {
            member.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for (ClassBodyMember member : classDecl.body) {
            member.accept(this);
        }
    }

    @Override
    public void visit(FieldDecl fieldDecl) {
        // Si es necesario, maneja las declaraciones de campos aquí
        // Por ahora, no generamos código IR para FieldDecl a menos que necesites inicialización
    }

    @Override
    public void visit(VarDecl varDecl) {
        TEMP varTemp = new TEMP(varDecl.name);

        if (varDecl.initExpr != null) {
            IRExp initValue = translateExpression(varDecl.initExpr);
            irStatements.add(new MOVE(varTemp, initValue));
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        currentMethod = methodDecl.name;
        inMethod = true;

        String methodLabel = methodDecl.name;
        irStatements.add(new LABEL(methodLabel));

        generatePrologue(methodDecl);

        methodDecl.body.accept(this);

        generateEpilogue(methodDecl);

        inMethod = false;
        currentMethod = null;
    }

    @Override
    public void visit(Block block) {
        for (VarDecl varDecl : block.varDecls) {
            varDecl.accept(this);
        }

        for (Statement stmt : block.statements) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(AssignStmt assignStmt) {
        IRExp dest = translateLocation(assignStmt.location);
        IRExp src = translateExpression(assignStmt.expr);
        irStatements.add(new MOVE(dest, src));
    }

    @Override
    public void visit(MethodCallStmt methodCallStmt) {
        methodCallStmt.getMethodCall().accept(this);
    }

    @Override
    public void visit(IfStmt ifStmt) {
        String trueLabel = newLabel("if_true");
        String falseLabel = newLabel("if_false");
        String endLabel = newLabel("if_end");

        IRExp condition = translateExpression(ifStmt.getCondition());
        irStatements.add(new CJUMP(BinOp.NE, condition, new CONST(0), trueLabel, falseLabel));

        irStatements.add(new LABEL(trueLabel));
        if (ifStmt.getThenBlock() != null) {
            ifStmt.getThenBlock().accept(this);
        }
        irStatements.add(new JUMP(new NAME(endLabel)));

        irStatements.add(new LABEL(falseLabel));
        if (ifStmt.getElseBlock() != null) {
            ifStmt.getElseBlock().accept(this);
        }

        irStatements.add(new LABEL(endLabel));
    }

    @Override
    public void visit(WhileStmt whileStmt) {
        String testLabel = newLabel("while_test");
        String bodyLabel = newLabel("while_body");
        String endLabel = newLabel("while_end");

        currentLoop.push(endLabel);

        irStatements.add(new LABEL(testLabel));

        IRExp condition = translateExpression(whileStmt.getCondition());
        irStatements.add(new CJUMP(BinOp.NE, condition, new CONST(0), bodyLabel, endLabel));

        irStatements.add(new LABEL(bodyLabel));
        whileStmt.getBody().accept(this);
        irStatements.add(new JUMP(new NAME(testLabel)));

        irStatements.add(new LABEL(endLabel));

        currentLoop.pop();
    }

    @Override
    public void visit(ForStmt forStmt) {
        String initLabel = newLabel("for_init");
        String testLabel = newLabel("for_test");
        String bodyLabel = newLabel("for_body");
        String updateLabel = newLabel("for_update");
        String endLabel = newLabel("for_end");

        currentLoop.push(endLabel);

        // Inicialización
        irStatements.add(new LABEL(initLabel));
        if (forStmt.getInit() != null) {
            forStmt.getInit().accept(this);
        }

        // Prueba de condición
        irStatements.add(new LABEL(testLabel));
        IRExp condition = translateExpression(forStmt.getCondition());
        irStatements.add(new CJUMP(BinOp.NE, condition, new CONST(0), bodyLabel, endLabel));

        // Cuerpo
        irStatements.add(new LABEL(bodyLabel));
        forStmt.getBody().accept(this);

        // Actualización
        if (forStmt.getUpdate() != null) {
            irStatements.add(new LABEL(updateLabel));
            forStmt.getUpdate().accept(this);
        }
        irStatements.add(new JUMP(new NAME(testLabel)));

        // Fin del for
        irStatements.add(new LABEL(endLabel));

        currentLoop.pop();
    }

    @Override
    public void visit(ReturnStmt returnStmt) {
        if (returnStmt.getExpression() != null) {
            IRExp returnValue = translateExpression(returnStmt.getExpression());
            irStatements.add(new MOVE(new TEMP("return"), returnValue));
        }
        irStatements.add(new JUMP(new NAME(currentMethod + "_end")));
    }

    @Override
    public void visit(BreakStmt breakStmt) {
        if (!currentLoop.isEmpty()) {
            irStatements.add(new JUMP(new NAME(currentLoop.peek())));
        }
    }

    @Override
    public void visit(ContinueStmt continueStmt) {
        if (!currentLoop.isEmpty()) {
            String loopStartLabel = currentLoop.peek().replace("end", "test");
            irStatements.add(new JUMP(new NAME(loopStartLabel)));
        }
    }

    @Override
    public void visit(ExprStmt exprStmt) {
        IRExp expr = translateExpression(exprStmt.getExpression());
        irStatements.add(new EXPR(expr));
    }

    @Override
    public void visit(VarDeclStmt varDeclStmt) {
        varDeclStmt.getVarDecl().accept(this);
        if (varDeclStmt.getInitExpression() != null) {
            IRExp initValue = translateExpression(varDeclStmt.getInitExpression());
            TEMP varTemp = new TEMP(varDeclStmt.getVarDecl().name);
            irStatements.add(new MOVE(varTemp, initValue));
        }
    }

    @Override
    public void visit(AssignExpr assignExpr) {
        IRExp dest = translateLocation(assignExpr.getLocation());
        IRExp src = translateExpression(assignExpr.getExpression());
        irStatements.add(new MOVE(dest, src));
    }

    @Override
    public void visit(MethodCall methodCall) {
        List<IRExp> args = new ArrayList<>();

        for (Expression arg : methodCall.getArguments()) {
            args.add(translateExpression(arg));
        }

        IRExp callExp = new CALL(new NAME(methodCall.getMethodName()), args);

        if (inMethod) {
            TEMP temp = new TEMP(newTemp());
            irStatements.add(new MOVE(temp, callExp));
        } else {
            irStatements.add(new EXPR(callExp));
        }
    }

    @Override
    public void visit(CalloutStmt calloutStmt) {
        CalloutCall call = calloutStmt.getCalloutCall();
        List<IRExp> args = new ArrayList<>();

        for (CalloutArg arg : call.getArgs()) {
            if (arg instanceof ExprArg) {
                args.add(translateExpression(((ExprArg) arg).getExpression()));
            } else if (arg instanceof StringArg) {
                String strLabel = newLabel("str");
                // Agrega la cadena a una tabla de cadenas si es necesario
                args.add(new NAME(strLabel));
            }
        }

        irStatements.add(new EXPR(new CALL(new NAME("callout_" + call.getName()), args)));
    }

    @Override
    public void visit(CalloutCall calloutCall) {
        // Ya manejado en visit(CalloutStmt)
    }

    @Override
    public void visit(NewArrayExpr newArrayExpr) {
        IRExp size = translateExpression(newArrayExpr.getSize());
        IRExp byteSize = new BinOpExpr(BinOp.MUL, size, new CONST(4));
        List<IRExp> mallocArgs = new ArrayList<>();
        mallocArgs.add(byteSize);
        CALL mallocCall = new CALL(new NAME("malloc"), mallocArgs);
        TEMP arrayTemp = new TEMP(newTemp());
        irStatements.add(new MOVE(arrayTemp, mallocCall));
    }

    @Override
    public void visit(BinaryExpr binaryExpr) {
        // No se necesita implementar aquí, se maneja en translateExpression
    }

    @Override
    public void visit(UnaryExpr unaryExpr) {
        // No se necesita implementar aquí, se maneja en translateExpression
    }

    @Override
    public void visit(IntLiteral intLiteral) {
        // No se necesita implementar aquí, se maneja en translateExpression
    }

    @Override
    public void visit(BoolLiteral boolLiteral) {
        // No se necesita implementar aquí, se maneja en translateExpression
    }

    @Override
    public void visit(CharLiteral charLiteral) {
        // Manejo de literales de caracteres si es necesario
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        // Manejo de literales de cadena si es necesario
    }

    @Override
    public void visit(VarLocation varLocation) {
        // No se necesita implementar aquí, se maneja en translateLocation
    }

    @Override
    public void visit(ArrayLocation arrayLocation) {
        // No se necesita implementar aquí, se maneja en translateLocation
    }

    @Override
    public void visit(ExprArg exprArg) {
        // Ya manejado en visit(CalloutStmt)
    }

    @Override
    public void visit(StringArg stringArg) {
        // Ya manejado en visit(CalloutStmt)
    }

    @Override
    public void visit(Param param) {
        // Si es necesario, maneja parámetros aquí
    }

    @Override
    public void visit(MultiVarDecl multiVarDecl) {
        for (ClassBodyMember decl : multiVarDecl.getDeclarations()) {
            decl.accept(this);
        }
    }

    @Override
    public void visit(NullType nullType) {
        // Manejo del tipo null si es necesario
    }

    @Override
    public void visit(IntType intType) {
        // No se necesita implementación específica
    }

    @Override
    public void visit(BooleanType booleanType) {
        // No se necesita implementación específica
    }

    @Override
    public void visit(CharType charType) {
        // No se necesita implementación específica
    }

    @Override
    public void visit(VoidType voidType) {
        // No se necesita implementación específica
    }

    @Override
    public void visit(ArrayType arrayType) {
        // No se necesita implementación específica
    }

    @Override
    public void visit(StringType stringType) {
    }

    // Métodos auxiliares para traducir expresiones
    private IRExp translateExpression(Expression expr) {
        if (expr instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) expr;
            IRExp left = translateExpression(binExpr.left);
            IRExp right = translateExpression(binExpr.right);
            return new BinOpExpr(translateOperator(binExpr.op), left, right);
        } else if (expr instanceof IntLiteral) {
            return new CONST(((IntLiteral) expr).getValue());
        } else if (expr instanceof VarLocation) {
            return translateLocation((VarLocation) expr);
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) expr;
            IRExp operand = translateExpression(unaryExpr.expr);
            switch (unaryExpr.op) {
                case "-":
                    return new BinOpExpr(BinOp.MINUS, new CONST(0), operand);
                case "!":
                    return new BinOpExpr(BinOp.XOR, operand, new CONST(1));
                default:
                    throw new RuntimeException("Operador unario no soportado: " + unaryExpr.op);
            }
        } else if (expr instanceof BoolLiteral) {
            return new CONST(((BoolLiteral) expr).getValue() ? 1 : 0);
        } else if (expr instanceof MethodCall) {
            List<IRExp> args = new ArrayList<>();
            for (Expression arg : ((MethodCall) expr).getArguments()) {
                args.add(translateExpression(arg));
            }
            return new CALL(new NAME(((MethodCall) expr).getMethodName()), args);
        } else if (expr instanceof AssignExpr) {
            AssignExpr assignExpr = (AssignExpr) expr;
            IRExp dest = translateLocation(assignExpr.getLocation());
            IRExp src = translateExpression(assignExpr.getExpression());
            irStatements.add(new MOVE(dest, src));
            return dest;
        } else if (expr instanceof NewArrayExpr) {
            NewArrayExpr newArrayExpr = (NewArrayExpr) expr;
            IRExp size = translateExpression(newArrayExpr.getSize());
            IRExp byteSize = new BinOpExpr(BinOp.MUL, size, new CONST(4));
            List<IRExp> mallocArgs = new ArrayList<>();
            mallocArgs.add(byteSize);
            CALL mallocCall = new CALL(new NAME("malloc"), mallocArgs);
            TEMP arrayTemp = new TEMP(newTemp());
            irStatements.add(new MOVE(arrayTemp, mallocCall));
            return arrayTemp;
        } else if (expr instanceof ArrayLocation) {
            return translateLocation((ArrayLocation) expr);
        } else if (expr instanceof CharLiteral) {
            return new CONST((int)((CharLiteral) expr).value);
        } else if (expr instanceof StringLiteral) {
            String strLabel = newLabel("str");
            return new NAME(strLabel);
        }
        return null;
    }

        private BinOp translateOperator(String op) {
        switch (op) {
            case "+":
                return BinOp.PLUS;
            case "-":
                return BinOp.MINUS;
            case "*":
                return BinOp.MUL;
            case "/":
                return BinOp.DIV;
            case "&&":
                return BinOp.AND;
            case "||":
                return BinOp.OR;
            case "==":
                return BinOp.EQ;
            case "!=":
                return BinOp.NE;
            case "<":
                return BinOp.LT;
            case "<=":
                return BinOp.LE;
            case ">":
                return BinOp.GT;
            case ">=":
                return BinOp.GE;
            default:
                throw new RuntimeException("Operador no soportado: " + op);
        }
    }


    private IRExp translateLocation(Location loc) {
        if (loc instanceof VarLocation) {
            VarLocation varLoc = (VarLocation) loc;
            return new TEMP(varLoc.name);
        } else if (loc instanceof ArrayLocation) {
            ArrayLocation arrayLoc = (ArrayLocation) loc;
            IRExp index = translateExpression(arrayLoc.index);
            IRExp baseAddr = new TEMP(arrayLoc.name);
            IRExp offset = new BinOpExpr(BinOp.MUL, index, new CONST(4));
            return new MEM(new BinOpExpr(BinOp.PLUS, baseAddr, offset));
        }
        return null;
    }

    private void generatePrologue(MethodDecl methodDecl) {
        irStatements.add(new LABEL(currentMethod + "_prologue"));

    }

    private void generateEpilogue(MethodDecl methodDecl) {
        irStatements.add(new LABEL(currentMethod + "_end"));
        irStatements.add(new RETURN());
    }
}
