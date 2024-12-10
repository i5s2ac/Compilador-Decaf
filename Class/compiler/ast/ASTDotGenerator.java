package compiler.ast;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ASTDotGenerator implements ASTVisitor {
    private final PrintWriter writer;
    private final AtomicInteger nodeCounter;
    private final Map<Object, Integer> nodeIds;

    public ASTDotGenerator(PrintWriter writer) {
        this.writer = writer;
        this.nodeCounter = new AtomicInteger(0);
        this.nodeIds = new HashMap<>();
    }

    private int getNodeId(Object node) {
        return nodeIds.computeIfAbsent(node, k -> nodeCounter.incrementAndGet());
    }

    private void createNode(Object node, String label) {
        int id = getNodeId(node);
        // Escapar caracteres especiales en el label
        label = label.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        writer.printf("    node%d [label=\"%s\"];\n", id, label);
    }

    private void createEdge(Object from, Object to) {
        writer.printf("    node%d -> node%d;\n", getNodeId(from), getNodeId(to));
    }

    public void beginGraph() {
        writer.println("digraph AST {");
        writer.println("    node [shape=box, fontname=\"Arial\"];");
        writer.println("    edge [fontname=\"Arial\"];");
        writer.println("    rankdir=TB;");  // Top to bottom direction
    }

    public void endGraph() {
        writer.println("}");
    }

    @Override
    public void visit(Program program) {
        createNode(program, "Program\n" + program.className);
        for (ClassBodyMember member : program.classBody) {
            createEdge(program, member);
            member.accept(this);
        }
    }

    @Override
    public void visit(VarDecl varDecl) {
        String arrayStr = varDecl.isArray ? "[]" : "";
        createNode(varDecl, "VarDecl\n" + varDecl.name + ": " + varDecl.type.toString() + arrayStr);
        if (varDecl.initExpr != null) {
            createEdge(varDecl, varDecl.initExpr);
            varDecl.initExpr.accept(this);
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        createNode(methodDecl, "MethodDecl\n" + methodDecl.name + "\n" + methodDecl.returnType.toString());

        for (Param param : methodDecl.params) {
            createEdge(methodDecl, param);
            param.accept(this);
        }

        createEdge(methodDecl, methodDecl.body);
        methodDecl.body.accept(this);
    }

    @Override
    public void visit(Block block) {
        createNode(block, "Block");

        for (VarDecl varDecl : block.varDecls) {
            createEdge(block, varDecl);
            varDecl.accept(this);
        }

        for (Statement stmt : block.statements) {
            createEdge(block, stmt);
            stmt.accept(this);
        }
    }

    @Override
    public void visit(AssignStmt assignStmt) {
        createNode(assignStmt, "AssignStmt\n" + assignStmt.op);
        createEdge(assignStmt, assignStmt.location);
        createEdge(assignStmt, assignStmt.expr);
        assignStmt.location.accept(this);
        assignStmt.expr.accept(this);
    }

    @Override
    public void visit(MethodCallStmt methodCallStmt) {
        createNode(methodCallStmt, "MethodCallStmt");
        createEdge(methodCallStmt, methodCallStmt.getMethodCall());
        methodCallStmt.getMethodCall().accept(this);
    }

    @Override
    public void visit(IfStmt ifStmt) {
        createNode(ifStmt, "IfStmt");
        createEdge(ifStmt, ifStmt.getCondition());
        ifStmt.getCondition().accept(this);

        if (ifStmt.getThenBlock() != null) {
            createEdge(ifStmt, ifStmt.getThenBlock());
            ifStmt.getThenBlock().accept(this);
        }

        if (ifStmt.getElseBlock() != null) {
            createEdge(ifStmt, ifStmt.getElseBlock());
            ifStmt.getElseBlock().accept(this);
        }
    }

    @Override
    public void visit(WhileStmt whileStmt) {
        createNode(whileStmt, "WhileStmt");
        createEdge(whileStmt, whileStmt.getCondition());
        whileStmt.getCondition().accept(this);
        createEdge(whileStmt, whileStmt.getBody());
        whileStmt.getBody().accept(this);
    }

    @Override
    public void visit(ForStmt forStmt) {
        createNode(forStmt, "ForStmt");
        if (forStmt.getInit() != null) {
            createEdge(forStmt, forStmt.getInit());
            forStmt.getInit().accept(this);
        }
        if (forStmt.getCondition() != null) {
            createEdge(forStmt, forStmt.getCondition());
            forStmt.getCondition().accept(this);
        }
        if (forStmt.getUpdate() != null) {
            createEdge(forStmt, forStmt.getUpdate());
            forStmt.getUpdate().accept(this);
        }
        createEdge(forStmt, forStmt.getBody());
        forStmt.getBody().accept(this);
    }

    @Override
    public void visit(ReturnStmt returnStmt) {
        createNode(returnStmt, "ReturnStmt");
        if (returnStmt.getExpression() != null) {
            createEdge(returnStmt, returnStmt.getExpression());
            returnStmt.getExpression().accept(this);
        }
    }

    @Override
    public void visit(BreakStmt breakStmt) {
        createNode(breakStmt, "BreakStmt");
    }

    @Override
    public void visit(ContinueStmt continueStmt) {
        createNode(continueStmt, "ContinueStmt");
    }

    @Override
    public void visit(ExprStmt exprStmt) {
        createNode(exprStmt, "ExprStmt");
        createEdge(exprStmt, exprStmt.getExpression());
        exprStmt.getExpression().accept(this);
    }

    @Override
    public void visit(VarDeclStmt varDeclStmt) {
        createNode(varDeclStmt, "VarDeclStmt");
        createEdge(varDeclStmt, varDeclStmt.getVarDecl());
        varDeclStmt.getVarDecl().accept(this);
        if (varDeclStmt.getInitExpression() != null) {
            createEdge(varDeclStmt, varDeclStmt.getInitExpression());
            varDeclStmt.getInitExpression().accept(this);
        }
    }

    @Override
    public void visit(AssignExpr assignExpr) {
        createNode(assignExpr, "AssignExpr\n" + assignExpr.getOperator());
        createEdge(assignExpr, assignExpr.getLocation());
        createEdge(assignExpr, assignExpr.getExpression());
        assignExpr.getLocation().accept(this);
        assignExpr.getExpression().accept(this);
    }

    

    @Override
    public void visit(FieldDecl fieldDecl) {
        createNode(fieldDecl, "FieldDecl\n" + fieldDecl.name + ": " + fieldDecl.type.toString());
        // Si necesitas agregar más lógica, hazlo aquí
    }

    @Override
    public void visit(ClassDecl classDecl) {
        createNode(classDecl, "ClassDecl\n" + classDecl.name);
        for (ClassBodyMember member : classDecl.body) {
            createEdge(classDecl, member);
            member.accept(this);
        }
    }


    @Override
    public void visit(MethodCall methodCall) {
        createNode(methodCall, "MethodCall\n" + methodCall.getMethodName());
        for (Expression arg : methodCall.getArguments()) {
            createEdge(methodCall, arg);
            arg.accept(this);
        }
    }

    @Override
    public void visit(CalloutStmt calloutStmt) {
        createNode(calloutStmt, "CalloutStmt");
        createEdge(calloutStmt, calloutStmt.getCalloutCall());
        calloutStmt.getCalloutCall().accept(this);
    }

    @Override
    public void visit(CalloutCall calloutCall) {
        createNode(calloutCall, "CalloutCall\n" + calloutCall.getFunctionName());
        for (CalloutArg arg : calloutCall.getCalloutArguments()) {
            createEdge(calloutCall, arg);
            arg.accept(this);
        }
    }

    @Override
    public void visit(NewArrayExpr newArrayExpr) {
        createNode(newArrayExpr, "NewArrayExpr");
        createEdge(newArrayExpr, newArrayExpr.getType());
        newArrayExpr.getType().accept(this);
        createEdge(newArrayExpr, newArrayExpr.getSize());
        newArrayExpr.getSize().accept(this);
    }

    @Override
    public void visit(BinaryExpr binaryExpr) {
        createNode(binaryExpr, "BinaryExpr\n" + binaryExpr.op);
        createEdge(binaryExpr, binaryExpr.left);
        createEdge(binaryExpr, binaryExpr.right);
        binaryExpr.left.accept(this);
        binaryExpr.right.accept(this);
    }

    @Override
    public void visit(UnaryExpr unaryExpr) {
        createNode(unaryExpr, "UnaryExpr\n" + unaryExpr.op);
        createEdge(unaryExpr, unaryExpr.expr);
        unaryExpr.expr.accept(this);
    }

    @Override
    public void visit(IntLiteral intLiteral) {
        createNode(intLiteral, "IntLiteral\n" + intLiteral.getValue());
    }

    @Override
    public void visit(BoolLiteral boolLiteral) {
        createNode(boolLiteral, "BoolLiteral\n" + boolLiteral.getValue());
    }

    @Override
    public void visit(CharLiteral charLiteral) {
        createNode(charLiteral, "CharLiteral\n'" + charLiteral.value + "'");
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        createNode(stringLiteral, "StringLiteral\n\"" + stringLiteral.getValue() + "\"");
    }

    @Override
    public void visit(VarLocation varLocation) {
        createNode(varLocation, "VarLocation\n" + varLocation.name);
    }

    @Override
    public void visit(ArrayLocation arrayLocation) {
        createNode(arrayLocation, "ArrayLocation\n" + arrayLocation.name);
        createEdge(arrayLocation, arrayLocation.index);
        arrayLocation.index.accept(this);
    }

    @Override
    public void visit(IntType intType) {
        createNode(intType, "IntType");
    }

    @Override
    public void visit(BooleanType booleanType) {
        createNode(booleanType, "BooleanType");
    }

    @Override
    public void visit(CharType charType) {
        createNode(charType, "CharType");
    }

    @Override
    public void visit(VoidType voidType) {
        createNode(voidType, "VoidType");
    }

    @Override
    public void visit(Param param) {
        String arrayStr = param.isArray ? "[]" : "";
        createNode(param, "Param\n" + param.name + ": " + param.type.toString() + arrayStr);
    }

    @Override
    public void visit(ExprArg exprArg) {
        createNode(exprArg, "ExprArg");
        createEdge(exprArg, exprArg.getExpression());
        exprArg.getExpression().accept(this);
    }

    @Override
    public void visit(StringArg stringArg) {
        createNode(stringArg, "StringArg\n\"" + stringArg.getValue() + "\"");
    }

    @Override
    public void visit(MultiVarDecl multiVarDecl) {
        createNode(multiVarDecl, "MultiVarDecl");
        for (ClassBodyMember decl : multiVarDecl.getDeclarations()) {
            createEdge(multiVarDecl, decl);
            decl.accept(this);
        }
    }

    @Override
    public void visit(StringType stringType) {
        // Implementa cómo se representará StringType en el gráfico DOT
        // Por ejemplo:
        System.out.println("\"StringType\" [label=\"String\"];");
    }

    @Override
    public void visit(NullType nullType) {
        // Implementa cómo se representará NullType en el gráfico DOT
        System.out.println("\"NullType\" [label=\"null\"];");
    }

    // Implementación de visit(ArrayType)
    @Override
    public void visit(ArrayType arrayType) {
        createNode(arrayType, "ArrayType\n" + arrayType.getElementType().toString() + "[]");
    }

    
}
