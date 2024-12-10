package compiler.irt;

/**
 * Enumeración de operaciones binarias para la IR.
 */
public enum BinOp {
    PLUS,    // Suma
    MINUS,   // Resta
    MUL,     // Multiplicación
    DIV,     // División
    AND,     // AND lógico
    OR,      // OR lógico
    EQ,      // Igualdad
    NE,      // Desigualdad
    LT,      // Menor que
    LE,      // Menor o igual que
    GT,      // Mayor que
    GE,      // Mayor o igual que
    XOR;     // XOR lógico

    @Override
    public String toString() {
        return this.name();
    }
}
