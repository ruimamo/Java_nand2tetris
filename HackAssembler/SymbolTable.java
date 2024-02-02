import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Integer> symbolTable;

    SymbolTable() {
        symbolTable = new HashMap<String, Integer>();
    }

    public void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }

}
