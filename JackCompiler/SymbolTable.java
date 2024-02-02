import java.util.HashMap;
import java.util.Map;

class SymbolTableRow {
    String type;
    String kind;
    int index;

    SymbolTableRow(String type, String kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }
}

public class SymbolTable {
    private Map<String, SymbolTableRow> classTable = new HashMap<>();
    private Map<String, SymbolTableRow> subroutineTable = new HashMap<>();

    SymbolTable() {
        classTable.clear();
        subroutineTable.clear();
    }

    public void showTable() {
        System.out.println("-- classTable --");
        for (Map.Entry<String, SymbolTableRow> entry : classTable.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().type
                    + " " + entry.getValue().kind + " " + entry.getValue().index);
        }

        System.out.println("-- subroutineTable --");
        for (Map.Entry<String, SymbolTableRow> entry : subroutineTable.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().type
                    + " " + entry.getValue().kind + " " + entry.getValue().index);
        }
    }

    public void startSubroutine() {
        subroutineTable.clear();
    }

    public void define(String name, String type, String kind) throws IllegalSymbolTableException {
        Map<String, SymbolTableRow> table;

        if (kind.equals("static") || kind.equals("field")) {
            table = classTable;
        } else if (kind.equals("arg") || kind.equals("var")) {
            table = subroutineTable;
        } else {
            throw new IllegalSymbolTableException(
                    "Symbol kind must be \"static\", \"field\", \"arg\" or \"var\"");
        }

        table.put(name, new SymbolTableRow(type, kind, varCount(kind)));
    }

    public int varCount(String kind) throws IllegalSymbolTableException {
        int count = 0;
        Map<String, SymbolTableRow> table;

        if (kind.equals("static") || kind.equals("field")) {
            table = classTable;
        } else if (kind.equals("arg") || kind.equals("var")) {
            table = subroutineTable;
        } else {
            throw new IllegalSymbolTableException("Symbol kind must be \"static\", \"field\", \"arg\" or \"var\"");
        }

        for (SymbolTableRow row : table.values()) {
            if (row.kind.equals(kind)) {
                count++;
            }
        }

        return count;
    }

    public String kindOf(String name) {
        if (classTable.containsKey(name)) {
            return classTable.get(name).kind;
        } else if (subroutineTable.containsKey(name)) {
            return subroutineTable.get(name).kind;
        } else {
            return "NONE";
        }
    }

    public String typeOf(String name) throws SymbolNotFoundException {
        if (classTable.containsKey(name)) {
            return classTable.get(name).type;
        } else if (subroutineTable.containsKey(name)) {
            return subroutineTable.get(name).type;
        } else {
            throw new SymbolNotFoundException("A symbol name \"" + name + "\" is not found in symbol table.");
        }
    }

    public int indexOf(String name) throws SymbolNotFoundException {
        if (classTable.containsKey(name)) {
            return classTable.get(name).index;
        } else if (subroutineTable.containsKey(name)) {
            return subroutineTable.get(name).index;
        } else {
            throw new SymbolNotFoundException("A symbol name \"" + name + "\" is not found in symbol table.");
        }
    }
}
