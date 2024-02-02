import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class Assembler {
    public static void main(String[] args) throws IOException {
        if (!args[0].matches("^.*\\.asm$")) {
            throw new IOException("read only .asm file");
        }
        
        File file = new File(args[0]);

        String outputFileName = file.getPath().replace(".asm", ".hack");
        File outputFile = new File(outputFileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        Parser parser = new Parser(file);
        Code code = new Code();

        SymbolTable symbolTable = new SymbolTable();

        addDefinedSymbol(symbolTable);

        addLabelSymbol(parser, symbolTable);

        parser = new Parser(file);

        Pattern decimal = Pattern.compile("^[0-9]+$");
        int usableAddress = 16;

        try {
            while (parser.hasMoreCommands()) {
                String binaryLine = "";
                parser.advance();

                if (parser.commandType() == EnumCommands.A_COMMAND) {
                    if (decimal.matcher(parser.symbol()).matches()) {
                        binaryLine = String.format("%16s", Integer.toBinaryString(Integer.parseInt(parser.symbol())))
                                .replace(" ", "0");
                        bw.write(binaryLine);
                        bw.newLine();
                    } else if (symbolTable.contains(parser.symbol())) {
                        binaryLine = String
                                .format("%16s", Integer.toBinaryString(symbolTable.getAddress(parser.symbol())))
                                .replace(" ", "0");
                        bw.write(binaryLine);
                        bw.newLine();
                    } else {
                        symbolTable.addEntry(parser.symbol(), usableAddress);
                        usableAddress++;
                        binaryLine = String
                                .format("%16s", Integer.toBinaryString(symbolTable.getAddress(parser.symbol())))
                                .replace(" ", "0");
                        bw.write(binaryLine);
                        bw.newLine();
                    }
                } else if (parser.commandType() == EnumCommands.C_COMMAND) {
                    binaryLine = "111" + code.comp(parser.comp()) + code.dest(parser.dest()) + code.jump(parser.jump());
                    bw.write(binaryLine);
                    bw.newLine();
                }
            }
        } catch (IllegalCommandException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            parser.closeBufferedReader();
        }

        bw.close();
    }

    public static void addDefinedSymbol(SymbolTable symbolTable) {
        symbolTable.addEntry("SP", 0);
        symbolTable.addEntry("LCL", 1);
        symbolTable.addEntry("ARG", 2);
        symbolTable.addEntry("THIS", 3);
        symbolTable.addEntry("THAT", 4);
        for (int i = 0; i < 16; i++) {
            symbolTable.addEntry("R" + i, i);
        }
        symbolTable.addEntry("SCREEN", 16384);
        symbolTable.addEntry("KBD", 24576);
    }

    public static void addLabelSymbol(Parser parser, SymbolTable symbolTable) {
        int rowNumber = -1;

        try {
            while (parser.hasMoreCommands()) {
                parser.advance();
                if (parser.commandType() == EnumCommands.A_COMMAND || parser.commandType() == EnumCommands.C_COMMAND) {
                    rowNumber++;
                } else if (parser.commandType() == EnumCommands.L_COMMAND) {
                    symbolTable.addEntry(parser.symbol(), rowNumber + 1);
                }
            }
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        } finally {
            parser.closeBufferedReader();
        }
    }
}
