package compiler.ast;

public class Variable {
    private String name;
    private boolean isArray;

    public Variable(String name, boolean isArray) {
        this.name = name;
        this.isArray = isArray;
    }

    public String getName() {
        return name;
    }

    public boolean isArray() {
        return isArray;
    }
}
