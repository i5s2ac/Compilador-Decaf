package compiler.ast;

public interface ASTVisitor {
    // Clases principales
    void visit(Program program);
    void visit(VarDecl varDecl);
    void visit(ClassDecl classDecl); 
    void visit(FieldDecl fieldDecl);
    void visit(MethodDecl methodDecl);
    void visit(MultiVarDecl multiVarDecl);
    void visit(Block block);
    void visit(Param param);

    // Sentencias
    void visit(AssignStmt assignStmt);
    void visit(MethodCallStmt methodCallStmt);
    void visit(IfStmt ifStmt);
    void visit(WhileStmt whileStmt);
    void visit(ForStmt forStmt);
    void visit(ReturnStmt returnStmt);
    void visit(BreakStmt breakStmt);
    void visit(ContinueStmt continueStmt);
    void visit(ExprStmt exprStmt);
    void visit(VarDeclStmt varDeclStmt);
    void visit(CalloutStmt calloutStmt);

    // Expresiones
    void visit(AssignExpr assignExpr);
    void visit(BinaryExpr binaryExpr);
    void visit(UnaryExpr unaryExpr);
    void visit(MethodCall methodCall);
    void visit(CalloutCall calloutCall);
    void visit(NewArrayExpr newArrayExpr);
    

    // Argumentos de callout
    void visit(ExprArg exprArg);
    void visit(StringArg stringArg);

    // Literales
    void visit(IntLiteral intLiteral);
    void visit(BoolLiteral boolLiteral);
    void visit(CharLiteral charLiteral);
    void visit(StringLiteral stringLiteral);

    // Ubicaciones
    void visit(VarLocation varLocation);
    void visit(ArrayLocation arrayLocation);

    // Tipos
    void visit(IntType intType);
    void visit(BooleanType booleanType);
    void visit(CharType charType);
    void visit(VoidType voidType); 
    void visit(ArrayType arrayType);
    void visit(StringType stringType);
    void visit(NullType nullType);


}
