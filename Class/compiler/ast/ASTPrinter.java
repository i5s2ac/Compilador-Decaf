package compiler.ast;

import java.io.PrintWriter;
import java.util.List;

public class ASTPrinter implements ASTVisitor {
    private PrintWriter writer;
    private int indent = 0;

    public ASTPrinter(PrintWriter writer) {
        this.writer = writer;
    }

    private void println(String s) {
        for (int i = 0; i < indent; i++) {
            writer.print("  ");
        }
        writer.println(s);
    }

    private void indent() {
        indent++;
    }

    private void unindent() {
        if (indent > 0) indent--;
    }

    @Override
    public void visit(Program program) {
        println("Program: " + program.className);
        indent();
        for (ClassBodyMember member : program.classBody) {
            member.accept(this);
        }
        unindent();
    }

    @Override
    public void visit(FieldDecl fieldDecl) {
        println("FieldDecl: " + fieldDecl.name + " Type: " + fieldDecl.type.toString());
       
    }

    @Override
    public void visit(ClassDecl classDecl) {
        println("ClassDecl: " + classDecl.name);
        indent();
        for (ClassBodyMember member : classDecl.body) {
            member.accept(this);
        }
        unindent();
    }

    @Override
    public void visit(VarDecl varDecl) {
        println("VarDecl: " + varDecl.name + " Type: " + varDecl.type.toString() + (varDecl.isArray ? "[]" : ""));
        if (varDecl.initExpr != null) {
            indent();
            println("InitExpr:");
            indent();
            varDecl.initExpr.accept(this);
            unindent();
            unindent();
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        println("MethodDecl: " + methodDecl.name + " ReturnType: " + methodDecl.returnType.toString());
        indent();
        println("Parameters:");
        indent();
        for (Param param : methodDecl.params) {
            println("Param: " + param.name + " Type: " + param.type.toString() + (param.isArray ? "[]" : ""));
        }
        unindent();
        println("Body:");
        methodDecl.body.accept(this);
        unindent();
    }

    @Override
    public void visit(Block block) {
        println("Block:");
        indent();
        println("VarDecls:");
        indent();
        for (VarDecl varDecl : block.varDecls) {
            varDecl.accept(this);
        }
        unindent();
        println("Statements:");
        indent();
        for (Statement stmt : block.statements) {
            stmt.accept(this);
        }
        unindent();
        unindent();
    }

    @Override
    public void visit(AssignStmt assignStmt) {
        println("AssignStmt:");
        indent();
        println("Location:");
        indent();
        assignStmt.location.accept(this);
        unindent();
        println("Operator: " + assignStmt.op);
        println("Expression:");
        indent();
        assignStmt.expr.accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(MethodCallStmt methodCallStmt) {
        println("MethodCallStmt:");
        indent();
        methodCallStmt.getMethodCall().accept(this);
        unindent();
    }

    @Override
    public void visit(IfStmt ifStmt) {
        println("IfStmt:");
        indent();
        println("Condition:");
        indent();
        ifStmt.getCondition().accept(this);
        unindent();
        println("Then Block:");
        indent();
        if (ifStmt.getThenBlock() != null) { // Añadido 'if'
            ifStmt.getThenBlock().accept(this);
        } else {
            println("None");
        }
        unindent();
        if (ifStmt.getElseBlock() != null) { // Añadido 'if' para el else
            println("Else Block:");
            indent();
            ifStmt.getElseBlock().accept(this);
            unindent();
        }
        unindent();
    }

    @Override
    public void visit(WhileStmt whileStmt) {
        println("WhileStmt:");
        indent();
        println("Condition:");
        indent();
        whileStmt.getCondition().accept(this);
        unindent();
        println("Body:");
        indent();
        whileStmt.getBody().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(ForStmt forStmt) {
        println("ForStmt:");
        indent();
        if (forStmt.getInit() != null) {
            println("Initialization:");
            indent();
            forStmt.getInit().accept(this);
            unindent();
        }
        if (forStmt.getCondition() != null) {
            println("Condition:");
            indent();
            forStmt.getCondition().accept(this);
            unindent();
        }
        if (forStmt.getUpdate() != null) {
            println("Update:");
            indent();
            forStmt.getUpdate().accept(this);
            unindent();
        }
        println("Body:");
        indent();
        forStmt.getBody().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(ReturnStmt returnStmt) {
        println("ReturnStmt:");
        indent();
        if (returnStmt.getExpression() != null) {
            returnStmt.getExpression().accept(this);
        } else {
            println("void");
        }
        unindent();
    }

    @Override
    public void visit(BreakStmt breakStmt) {
        println("BreakStmt");
    }

    @Override
    public void visit(ContinueStmt continueStmt) {
        println("ContinueStmt");
    }

    @Override
    public void visit(ExprStmt exprStmt) {
        println("ExprStmt:");
        indent();
        exprStmt.getExpression().accept(this);
        unindent();
    }

    @Override
    public void visit(VarDeclStmt varDeclStmt) {
        println("VarDeclStmt:");
        indent();
        varDeclStmt.getVarDecl().accept(this);
        if (varDeclStmt.getInitExpression() != null) {
            println("InitExpression:");
            indent();
            varDeclStmt.getInitExpression().accept(this);
            unindent();
        }
        unindent();
    }

    @Override
    public void visit(AssignExpr assignExpr) {
        println("AssignExpr:");
        indent();
        println("Location:");
        indent();
        assignExpr.getLocation().accept(this);
        unindent();
        println("Operator: " + assignExpr.getOperator());
        println("Expression:");
        indent();
        assignExpr.getExpression().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(MethodCall methodCall) {
        println("MethodCall: " + methodCall.getMethodName());
        indent();
        println("Arguments:");
        indent();
        for (Expression arg : methodCall.getArguments()) {
            arg.accept(this);
        }
        unindent();
        unindent();
    }

    @Override
    public void visit(CalloutStmt calloutStmt) {
        println("CalloutStmt:");
        indent();
        calloutStmt.getCalloutCall().accept(this);
        unindent();
    }

    @Override
    public void visit(CalloutCall calloutCall) {
        println("CalloutCall: " + calloutCall.getFunctionName());
        indent();
        println("Arguments:");
        indent();
        for (CalloutArg arg : calloutCall.getCalloutArguments()) {
            arg.accept(this);
        }
        unindent();
        unindent();
    }

    @Override
    public void visit(NewArrayExpr newArrayExpr) {
        println("NewArrayExpr:");
        indent();
        println("Type:");
        indent();
        newArrayExpr.getType().accept(this);
        unindent();
        println("Size:");
        indent();
        newArrayExpr.getSize().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(BinaryExpr binaryExpr) {
        println("BinaryExpr: " + binaryExpr.op);
        indent();
        println("Left:");
        indent();
        binaryExpr.left.accept(this);
        unindent();
        println("Right:");
        indent();
        binaryExpr.right.accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(UnaryExpr unaryExpr) {
        println("UnaryExpr: " + unaryExpr.op);
        indent();
        unaryExpr.expr.accept(this);
        unindent();
    }

    @Override
    public void visit(IntLiteral intLiteral) {
        println("IntLiteral: " + intLiteral.getValue());
    }

    @Override
    public void visit(BoolLiteral boolLiteral) {
        println("BoolLiteral: " + boolLiteral.getValue());
    }

    @Override
    public void visit(CharLiteral charLiteral) {
        println("CharLiteral: '" + charLiteral.value + "'");
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        println("StringLiteral: \"" + stringLiteral.getValue() + "\"");
    }

    @Override
    public void visit(VarLocation varLocation) {
        println("VarLocation: " + varLocation.name);
    }

    @Override
    public void visit(ArrayLocation arrayLocation) {
        println("ArrayLocation: " + arrayLocation.name);
        indent();
        println("Index:");
        indent();
        arrayLocation.index.accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(IntType intType) {
        println("Type: int");
    }

    @Override
    public void visit(BooleanType booleanType) {
        println("Type: boolean");
    }

    @Override
    public void visit(CharType charType) {
        println("Type: char");
    }

    @Override
    public void visit(VoidType voidType) {
        println("Type: void");
    }

    @Override
    public void visit(Param param) {
        println("Param: " + param.name + " Type: " + param.type.toString() + (param.isArray ? "[]" : ""));
    }

    @Override
    public void visit(ExprArg exprArg) {
        println("ExprArg:");
        indent();
        exprArg.getExpression().accept(this);
        unindent();
    }

    @Override
    public void visit(StringArg stringArg) {
        println("StringArg: \"" + stringArg.getValue() + "\"");
    }

    @Override
    public void visit(MultiVarDecl multiVarDecl) {
        for (ClassBodyMember decl : multiVarDecl.getDeclarations()) {
            decl.accept(this);
        }
    }

    // Implementación de visit(ArrayType)
    @Override
    public void visit(ArrayType arrayType) {
        println("ArrayType: " + arrayType.toString());
    }

    @Override
    public void visit(StringType stringType) {
        System.out.println("Tipo: String");
    }

    @Override
    public void visit(NullType nullType) {
        System.out.println("Tipo: null");
    }
    

}
