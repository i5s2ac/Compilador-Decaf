package compiler.semantic;

import compiler.ast.*;
import java.util.*;

/**
 * Maneja múltiples scopes y permite declarar y buscar símbolos.
 */
public class SymbolTable {
    private Deque<Map<String, Symbol>> scopes;

    /**
     * Constructor que inicializa la tabla de símbolos con un scope global
     * y registra métodos integrados como 'print'.
     */
    public SymbolTable() {
        scopes = new ArrayDeque<>();
        // Añadir el scope global
        enterScope();
        // Registrar métodos integrados
        registerBuiltInMethods();
    }

    /**
     * Entra en un nuevo scope.
     */
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Sale del scope actual.
     *
     * @throws EmptyStackException Si no hay scopes para salir.
     */
    public void exitScope() {
        if (scopes.isEmpty()) {
            throw new EmptyStackException();
        }
        scopes.pop();
    }

    /**
     * Declara un símbolo en el scope actual.
     *
     * @param symbol Símbolo a declarar.
     * @return True si la declaración fue exitosa, false si ya existe.
     */
    public boolean declare(Symbol symbol) {
        Map<String, Symbol> currentScope = scopes.peek();

        if (currentScope.containsKey(symbol.getName())) {
            return false; // Ya existe un símbolo con ese nombre en el scope actual
        }

        currentScope.put(symbol.getName(), symbol);
        return true;
    }

    /**
     * Busca un símbolo por su nombre.
     *
     * @param name Nombre del símbolo.
     * @return El símbolo encontrado o null si no existe.
     */
    public Symbol lookup(String name) {
        // Itera sobre los scopes desde el más interno hasta el más externo
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // No encontrado
    }

    /**
     * Registra métodos integrados como 'print' en el scope global.
     */
    private void registerBuiltInMethods() {
        // Método print(int)
        Symbol printInt = new Symbol("print", new VoidType(), Symbol.SymbolType.METHOD);
        printInt.addParameterType(new IntType());
        declare(printInt);

        // Método print(String)
        Symbol printString = new Symbol("print", new VoidType(), Symbol.SymbolType.METHOD);
        printString.addParameterType(new StringType());
        declare(printString);

        // Método print(char)
        Symbol printChar = new Symbol("print", new VoidType(), Symbol.SymbolType.METHOD);
        printChar.addParameterType(new CharType());
        declare(printChar);
    }


public void printAllScopes() {
    int scopeLevel = scopes.size();
    for (Map<String, Symbol> scope : scopes) {
        System.out.println("Scope nivel " + scopeLevel + ":");
        for (String name : scope.keySet()) {
            Symbol symbol = scope.get(name);
            System.out.println("- " + name + " (" + symbol.getSymbolType() + ", tipo: " + symbol.getType() + ")");
        }
        scopeLevel--;
    }
}


}
