import java.io.File;
import java.io.IOException;

public class CompilationEngine {
    private JackTokenizer jackTokenizer;
    private VMwriter vMwriter;
    private SymbolTable symbolTable;

    private String className;
    private int numOfLocalVariable;
    private int numOfExpression;
    private int numOfField;

    private int labelNumberOfWhile;
    private int labelNumberOfIf;

    CompilationEngine(File inputFile) {
        jackTokenizer = new JackTokenizer(inputFile);
        try {
            String outputFilePath = inputFile.getPath().substring(0, inputFile.getPath().length() - 5) + ".vm";
            File outputFile = new File(outputFilePath);

            vMwriter = new VMwriter(outputFile);

            labelNumberOfIf = 0;
            labelNumberOfWhile = 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        symbolTable = new SymbolTable();

        jackTokenizer.advance();
    }

    public void compileFile()
            throws IOException, IllegalTokenException, IllegalJackSyntaxException, IllegalSymbolTableException,
            SymbolNotFoundException {
        compileClass();
        symbolTable.showTable();
    }

    public void closeInputOutputFile() {
        jackTokenizer.close();
        try {
            vMwriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void compileClass()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, IllegalSymbolTableException,
            SymbolNotFoundException {

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("class")) {
            throw new IllegalJackSyntaxException("The first word of class must be \"class\". ");
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            throw new IllegalJackSyntaxException("The class name must be identifier.");
        }
        className = jackTokenizer.identifier();
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '{') {
            throw new IllegalJackSyntaxException("'{' is missing.");
        }
        jackTokenizer.advance();

        numOfField = 0;

        while (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("static")
                        || jackTokenizer.keyWord().equals("field"))) {
            compileClassVarDec();
        }

        while (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("constructor")
                        || jackTokenizer.keyWord().equals("function")
                        || jackTokenizer.keyWord().equals("method"))) {
            compileSubroutine();
        }

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '}') {
            throw new IllegalJackSyntaxException("'}' is not found.");
        }
        jackTokenizer.advance();

    }

    public void compileClassVarDec()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, IllegalSymbolTableException,
            SymbolNotFoundException {
        String name;
        String type;
        String kind;

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD
                || (!jackTokenizer.keyWord().equals("static")
                        && !jackTokenizer.keyWord().equals("field"))) {
            throw new IllegalJackSyntaxException("The first word of class variable must be \"static\" or \"field\".");
        }
        kind = jackTokenizer.keyWord();
        if (kind.equals("field")) {
            numOfField++;
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("int")
                        || jackTokenizer.keyWord().equals("char")
                        || jackTokenizer.keyWord().equals("boolean"))) {
            type = jackTokenizer.keyWord();
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.IDENTIFIER) {
            type = jackTokenizer.identifier();
            jackTokenizer.advance();
        } else {
            throw new IllegalJackSyntaxException("A class type is Illegal.");
        }

        if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            throw new IllegalJackSyntaxException("A class name must be identifier.");
        }
        name = jackTokenizer.identifier();
        symbolTable.define(name, type, kind);
        jackTokenizer.advance();

        while (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == ',') {
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                throw new IllegalJackSyntaxException("A class name must be identifier.");
            }
            name = jackTokenizer.identifier();
            symbolTable.define(name, type, kind);
            if (kind.equals("field")) {
                numOfField++;
            }
            jackTokenizer.advance();
        }

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ';') {
            throw new IllegalJackSyntaxException("';' is missing.");
        }
        jackTokenizer.advance();

    }

    public void compileSubroutine()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, IllegalSymbolTableException,
            SymbolNotFoundException {
        symbolTable.startSubroutine();

        boolean isConstructor = false;
        boolean isMethod = false;

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD
                || (!jackTokenizer.keyWord().equals("constructor")
                        && !jackTokenizer.keyWord().equals("function")
                        && !jackTokenizer.keyWord().equals("method"))) {
            throw new IllegalJackSyntaxException("The first word of subroutine"
                    + "must be \"constructor\" or \"function\" or \"method\".");
        }

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && jackTokenizer.keyWord().equals("constructor")) {
            isConstructor = true;
        }

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && jackTokenizer.keyWord().equals("method")) {
            isMethod = true;
            symbolTable.define("this", className, "arg");
        }

        jackTokenizer.advance();

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("void")
                        || jackTokenizer.keyWord().equals("int")
                        || jackTokenizer.keyWord().equals("char")
                        || jackTokenizer.keyWord().equals("boolean"))) {
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.IDENTIFIER) {
            jackTokenizer.advance();
        } else {
            throw new IllegalJackSyntaxException("A class type is Illegal.");
        }

        if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            throw new IllegalJackSyntaxException("A subroutine name must be identifier.");
        }
        String subroutineName = jackTokenizer.identifier();
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '(') {
            throw new IllegalJackSyntaxException("A parameter list must be surrounded '(' ')'. ");
        }
        jackTokenizer.advance();

        compileParameterList();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
            throw new IllegalJackSyntaxException("A parameter list must be surrounded '(' ')'. ");
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '{') {
            throw new IllegalJackSyntaxException("A subroutine body must be surrounded '{' '}'. ");
        }
        jackTokenizer.advance();

        numOfLocalVariable = 0;
        while (jackTokenizer.tokenType() == EnumToken.KEYWORD && (jackTokenizer.keyWord().equals("var"))) {
            compileVarDec();
        }

        vMwriter.writeFunction(className + "." + subroutineName, numOfLocalVariable);

        if (isConstructor) {
            vMwriter.writePush("constant", numOfField);
            vMwriter.writeCall("Memory.alloc", 1);
            vMwriter.writePop("pointer", 0);
        }

        if (isMethod) {
            vMwriter.writePush("argument", 0);
            vMwriter.writePop("pointer", 0);
        }

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD
                || (!jackTokenizer.keyWord().equals("let")
                        && !jackTokenizer.keyWord().equals("if")
                        && !jackTokenizer.keyWord().equals("while")
                        && !jackTokenizer.keyWord().equals("do")
                        && !jackTokenizer.keyWord().equals("return"))) {
            throw new IllegalJackSyntaxException("Statement is illegal.");
        }
        compileStatements();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '}') {
            throw new IllegalJackSyntaxException("A subroutine body must be surrounded '{' '}'. ");
        }
        jackTokenizer.advance();

    }

    public void compileParameterList()
            throws IllegalTokenException, IOException, IllegalJackSyntaxException, IllegalSymbolTableException,
            SymbolNotFoundException {
        String name;
        String type;

        if ((jackTokenizer.tokenType() != EnumToken.KEYWORD
                || (!jackTokenizer.keyWord().equals("int")
                        && !jackTokenizer.keyWord().equals("char")
                        && !jackTokenizer.keyWord().equals("boolean")))
                && jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            return;
        }

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("int")
                        || jackTokenizer.keyWord().equals("char")
                        || jackTokenizer.keyWord().equals("boolean"))) {
            type = jackTokenizer.keyWord();
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                throw new IllegalJackSyntaxException("A parameter name must be identifier.");
            }
            name = jackTokenizer.identifier();
            symbolTable.define(name, type, "arg");
            jackTokenizer.advance();

        } else if (jackTokenizer.tokenType() == EnumToken.IDENTIFIER) {
            type = jackTokenizer.identifier();
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                throw new IllegalJackSyntaxException("A parameter name must be identifier.");
            }
            name = jackTokenizer.identifier();
            symbolTable.define(name, type, "arg");
            jackTokenizer.advance();
        }

        while (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == ',') {
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                    && (jackTokenizer.keyWord().equals("int")
                            || jackTokenizer.keyWord().equals("char")
                            || jackTokenizer.keyWord().equals("boolean"))) {
                type = jackTokenizer.keyWord();
                jackTokenizer.advance();

                if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                    throw new IllegalJackSyntaxException("A parameter name must be identifier.");
                }
                name = jackTokenizer.identifier();
                symbolTable.define(name, type, "arg");
                jackTokenizer.advance();

            } else if (jackTokenizer.tokenType() == EnumToken.IDENTIFIER) {
                type = jackTokenizer.identifier();
                jackTokenizer.advance();

                if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                    throw new IllegalJackSyntaxException("A parameter name must be identifier.");
                }
                name = jackTokenizer.identifier();
                symbolTable.define(name, type, "arg");
                jackTokenizer.advance();
            } else {
                throw new IllegalJackSyntaxException("A parameter type is missing");
            }
        }

    }

    public void compileVarDec() throws IllegalJackSyntaxException, IllegalTokenException, IOException,
            IllegalSymbolTableException, SymbolNotFoundException {
        String name;
        String type;

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("var")) {
            throw new IllegalJackSyntaxException("the first word of variable declare must be \"var\" ");
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("int")
                        || jackTokenizer.keyWord().equals("char")
                        || jackTokenizer.keyWord().equals("boolean"))) {
            type = jackTokenizer.keyWord();
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.IDENTIFIER) {
            type = jackTokenizer.identifier();
            jackTokenizer.advance();
        } else {
            throw new IllegalJackSyntaxException("A type of declared variable is Illegal.");
        }

        if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            throw new IllegalJackSyntaxException("A name of declared variable must be identifier");
        }
        name = jackTokenizer.identifier();
        symbolTable.define(name, type, "var");
        jackTokenizer.advance();
        numOfLocalVariable++;

        while (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == ',') {
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                throw new IllegalJackSyntaxException("a name of declared variable must be identifier");
            }
            name = jackTokenizer.identifier();
            symbolTable.define(name, type, "var");
            jackTokenizer.advance();
            numOfLocalVariable++;
        }

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ';') {
            throw new IllegalJackSyntaxException("the end of variable declaring must be ';'. ");
        }
        jackTokenizer.advance();

    }

    public void compileStatements()
            throws IllegalTokenException, IOException, IllegalJackSyntaxException, SymbolNotFoundException {

        while (jackTokenizer.tokenType() == EnumToken.KEYWORD
                && (jackTokenizer.keyWord().equals("do")
                        || jackTokenizer.keyWord().equals("let")
                        || jackTokenizer.keyWord().equals("while")
                        || jackTokenizer.keyWord().equals("return")
                        || jackTokenizer.keyWord().equals("if"))) {
            if (jackTokenizer.keyWord().equals("do")) {
                compileDo();
            } else if (jackTokenizer.keyWord().equals("let")) {
                compileLet();
            } else if (jackTokenizer.keyWord().equals("while")) {
                compileWhile();
            } else if (jackTokenizer.keyWord().equals("return")) {
                compileReturn();
            } else if (jackTokenizer.keyWord().equals("if")) {
                compileIf();
            }
        }

    }

    public void compileDo()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, SymbolNotFoundException {

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("do")) {
            throw new IllegalJackSyntaxException("The first word of doStatement must be \"do\"");
        }
        jackTokenizer.advance();

        compileSubroutineCall();

        vMwriter.writePop("temp", 0);

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ';') {
            throw new IllegalJackSyntaxException("the end of subroutine must be ';'. ");
        }
        jackTokenizer.advance();

    }

    public void compileLet()
            throws IllegalJackSyntaxException, IllegalTokenException, IOException, SymbolNotFoundException {
        String kind;
        int index;

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("let")) {
            throw new IllegalJackSyntaxException("The first word of letStatement must be \"let\".");
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            throw new IllegalJackSyntaxException("A variable name of letStatement must be identifier.");
        }
        kind = symbolTable.kindOf(jackTokenizer.identifier());
        index = symbolTable.indexOf(jackTokenizer.identifier());
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == '[') {
            if (kind.equals("static")) {
                vMwriter.writePush("static", index);
            } else if (kind.equals("field")) {
                vMwriter.writePush("this", index);
            } else if (kind.equals("var")) {
                vMwriter.writePush("local", index);
            } else if (kind.equals("arg")) {
                vMwriter.writePush("argument", index);
            }

            jackTokenizer.advance();

            compileExpression();

            if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ']') {
                throw new IllegalJackSyntaxException("']' is missing.");
            }
            jackTokenizer.advance();

            vMwriter.writeArithmetic("add");

            kind = "that";
        }

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '=') {
            throw new IllegalJackSyntaxException("'=' is missing");
        }
        jackTokenizer.advance();

        compileExpression();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ';') {
            throw new IllegalJackSyntaxException("'=' is missing");
        }
        jackTokenizer.advance();

        if (kind.equals("static")) {
            vMwriter.writePop("static", index);
        } else if (kind.equals("field")) {
            vMwriter.writePop("this", index);
        } else if (kind.equals("var")) {
            vMwriter.writePop("local", index);
        } else if (kind.equals("arg")) {
            vMwriter.writePop("argument", index);
        } else if (kind.equals("that")) {
            vMwriter.writePop("temp", 0);
            vMwriter.writePop("pointer", 1);
            vMwriter.writePush("temp", 0);
            vMwriter.writePop("that", 0);
        }
    }

    public void compileWhile()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, SymbolNotFoundException {
        int labelNumber = labelNumberOfWhile;
        labelNumberOfWhile++;

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("while")) {
            throw new IllegalJackSyntaxException("The first word of whileExpression must be \"while\".");
        }
        jackTokenizer.advance();

        vMwriter.writeLabel("WHILE_EXP" + labelNumber);

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '(') {
            throw new IllegalJackSyntaxException("'(' is missing.");
        }
        jackTokenizer.advance();

        compileExpression();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
            throw new IllegalJackSyntaxException("')' is missing.");
        }
        jackTokenizer.advance();

        vMwriter.writeArithmetic("not");

        vMwriter.writeIf("WHILE_END" + labelNumber);

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '{') {
            throw new IllegalJackSyntaxException("'{' is missing.");
        }
        jackTokenizer.advance();

        compileStatements();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '}') {
            throw new IllegalJackSyntaxException("'}' is missing.");
        }
        jackTokenizer.advance();

        vMwriter.writeGoto("WHILE_EXP" + labelNumber);

        vMwriter.writeLabel("WHILE_END" + labelNumber);
    }

    public void compileReturn()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, SymbolNotFoundException {

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("return")) {
            throw new IllegalJackSyntaxException("the first word of returnExpression must be \"return\"");
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == ';') {
            vMwriter.writePush("constant", 0);
            vMwriter.writeReturn();
            jackTokenizer.advance();

        } else {
            compileExpression();

            if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ';') {
                throw new IllegalJackSyntaxException("';' is missing.");
            }
            vMwriter.writeReturn();
            jackTokenizer.advance();
        }

    }

    public void compileIf()
            throws IllegalTokenException, IllegalJackSyntaxException, IOException, SymbolNotFoundException {
        int labelNumber = labelNumberOfIf;
        labelNumberOfIf++;

        if (jackTokenizer.tokenType() != EnumToken.KEYWORD || !jackTokenizer.keyWord().equals("if")) {
            throw new IllegalJackSyntaxException("the first word of ifExpression must be \"if\"");
        }
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '(') {
            throw new IllegalJackSyntaxException("'(' is missing.");
        }
        jackTokenizer.advance();

        compileExpression();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
            throw new IllegalJackSyntaxException("')' is missing.");
        }
        jackTokenizer.advance();

        vMwriter.writeArithmetic("not");

        vMwriter.writeIf("IF_FALSE" + labelNumber);

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '{') {
            throw new IllegalJackSyntaxException("'{' is missing.");
        }
        jackTokenizer.advance();

        compileStatements();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '}') {
            throw new IllegalJackSyntaxException("'}' is missing.");
        }
        jackTokenizer.advance();

        vMwriter.writeGoto("IF_END" + labelNumber);

        vMwriter.writeLabel("IF_FALSE" + labelNumber);

        if (jackTokenizer.tokenType() == EnumToken.KEYWORD && jackTokenizer.keyWord().equals("else")) {
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '{') {
                throw new IllegalJackSyntaxException("'{' is missing.");
            }
            jackTokenizer.advance();

            compileStatements();

            if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '}') {
                throw new IllegalJackSyntaxException("'}' is missing.");
            }
            jackTokenizer.advance();
        }

        vMwriter.writeLabel("IF_END" + labelNumber);
    }

    public void compileExpression()
            throws IOException, IllegalTokenException, IllegalJackSyntaxException, SymbolNotFoundException {
        compileTerm();

        while (jackTokenizer.tokenType() == EnumToken.SYMBOL
                && (jackTokenizer.symbol() == '+'
                        || jackTokenizer.symbol() == '-'
                        || jackTokenizer.symbol() == '*'
                        || jackTokenizer.symbol() == '/'
                        || jackTokenizer.symbol() == '&'
                        || jackTokenizer.symbol() == '|'
                        || jackTokenizer.symbol() == '<'
                        || jackTokenizer.symbol() == '>'
                        || jackTokenizer.symbol() == '=')) {

            char tmpSymbol = jackTokenizer.symbol();
            jackTokenizer.advance();
            compileTerm();

            if (tmpSymbol == '+') {
                vMwriter.writeArithmetic("add");
            } else if (tmpSymbol == '-') {
                vMwriter.writeArithmetic("sub");
            } else if (tmpSymbol == '*') {
                vMwriter.writeCall("Math.multiply", 2);
            } else if (tmpSymbol == '/') {
                vMwriter.writeCall("Math.divide", 2);
            } else if (tmpSymbol == '&') {
                vMwriter.writeArithmetic("and");
            } else if (tmpSymbol == '|') {
                vMwriter.writeArithmetic("or");
            } else if (tmpSymbol == '<') {
                vMwriter.writeArithmetic("lt");
            } else if (tmpSymbol == '>') {
                vMwriter.writeArithmetic("gt");
            } else if (tmpSymbol == '=') {
                vMwriter.writeArithmetic("eq");
            }
        }
    }

    public void compileTerm()
            throws IllegalTokenException, IOException, IllegalJackSyntaxException, SymbolNotFoundException {

        if (jackTokenizer.tokenType() == EnumToken.INT_CONST) {
            vMwriter.writePush("constant", jackTokenizer.intVal());
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.STRING_CONST) {
            String stringVal = jackTokenizer.stringVal();
            vMwriter.writePush("constant", stringVal.length());
            vMwriter.writeCall("String.new", 1);

            for (int i = 0; i < stringVal.length(); i++) {
                vMwriter.writePush("constant", stringVal.charAt(i));
                vMwriter.writeCall("String.appendChar", 2);
            }
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.IDENTIFIER) {
            String tmpIdentifier = jackTokenizer.identifier();
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == '(') {

                jackTokenizer.advance();

                numOfExpression = 0;

                compileExpressionList();

                if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
                    throw new IllegalJackSyntaxException("expression list must be surrounded '(' ')'. ");
                }
                jackTokenizer.advance();

                vMwriter.writeCall(this.className + "." + tmpIdentifier, numOfExpression);
            } else if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == '.') {
                if (!symbolTable.kindOf(tmpIdentifier).equals("NONE")) {
                    if (symbolTable.kindOf(tmpIdentifier).equals("static")) {
                        vMwriter.writePush("static", symbolTable.indexOf(tmpIdentifier));
                    } else if (symbolTable.kindOf(tmpIdentifier).equals("field")) {
                        vMwriter.writePush("this", symbolTable.indexOf(tmpIdentifier));
                    } else if (symbolTable.kindOf(tmpIdentifier).equals("var")) {
                        vMwriter.writePush("local", symbolTable.indexOf(tmpIdentifier));
                    } else if (symbolTable.kindOf(tmpIdentifier).equals("arg")) {
                        vMwriter.writePush("argument", symbolTable.indexOf(tmpIdentifier));
                    }
                    tmpIdentifier = symbolTable.typeOf(tmpIdentifier);
                    numOfExpression = 1;
                } else {
                    numOfExpression = 0;
                }
                jackTokenizer.advance();

                if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                    throw new IllegalJackSyntaxException(
                            "subroutine name must be \"identifier\" or \"identifier.identifier\".");
                }
                tmpIdentifier = tmpIdentifier + "." + jackTokenizer.identifier();
                jackTokenizer.advance();

                if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '(') {
                    throw new IllegalJackSyntaxException("expression list must be surrounded '(' ')'. ");
                }
                jackTokenizer.advance();

                compileExpressionList();

                if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
                    throw new IllegalJackSyntaxException("expression list must be surrounded '(' ')'. ");
                }
                jackTokenizer.advance();

                vMwriter.writeCall(tmpIdentifier, numOfExpression);
            } else if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == '[') {
                String kind = symbolTable.kindOf(tmpIdentifier);
                int index = symbolTable.indexOf(tmpIdentifier);

                if (kind.equals("static")) {
                    vMwriter.writePush("static", index);
                } else if (kind.equals("field")) {
                    vMwriter.writePush("this", index);
                } else if (kind.equals("var")) {
                    vMwriter.writePush("local", index);
                } else if (kind.equals("arg")) {
                    vMwriter.writePush("argument", index);
                }
                jackTokenizer.advance();

                compileExpression();

                if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ']') {
                    throw new IllegalJackSyntaxException("']' is missing.");
                }
                jackTokenizer.advance();

                vMwriter.writeArithmetic("add");
                vMwriter.writePop("pointer", 1);
                vMwriter.writePush("that", 0);

            } else {
                String kind = symbolTable.kindOf(tmpIdentifier);
                if (kind.equals("static")) {
                    vMwriter.writePush("static", symbolTable.indexOf(tmpIdentifier));
                } else if (kind.equals("field")) {
                    vMwriter.writePush("this", symbolTable.indexOf(tmpIdentifier));
                } else if (kind.equals("arg")) {
                    vMwriter.writePush("argument", symbolTable.indexOf(tmpIdentifier));
                } else if (kind.equals("var")) {
                    vMwriter.writePush("local", symbolTable.indexOf(tmpIdentifier));
                }
            }
        } else if (jackTokenizer.tokenType() == EnumToken.KEYWORD) {
            if (jackTokenizer.keyWord().equals("true")) {
                vMwriter.writePush("constant", 1);
                vMwriter.writeArithmetic("neg");
            } else if (jackTokenizer.keyWord().equals("false")) {
                vMwriter.writePush("constant", 0);
            } else if (jackTokenizer.keyWord().equals("null")) {
                vMwriter.writePush("constant", 0);
            } else if (jackTokenizer.keyWord().equals("this")) {
                vMwriter.writePush("pointer", 0);
            } else {
                throw new IllegalJackSyntaxException(jackTokenizer.identifier() + " is not a term.");
            }
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == '(') {
            jackTokenizer.advance();

            compileExpression();

            if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
                throw new IllegalJackSyntaxException("')' is missing.");
            }
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == EnumToken.SYMBOL) {
            char symbol = jackTokenizer.symbol();
            jackTokenizer.advance();

            compileTerm();

            if (symbol == '-') {
                vMwriter.writeArithmetic("neg");
            } else if (symbol == '~') {
                vMwriter.writeArithmetic("not");
            }
        } else {
            throw new IllegalJackSyntaxException("Illegal term");
        }

    }

    public void compileExpressionList()
            throws IllegalTokenException, IOException, IllegalJackSyntaxException, SymbolNotFoundException {

        if (jackTokenizer.tokenType() == EnumToken.INT_CONST
                || jackTokenizer.tokenType() == EnumToken.STRING_CONST
                || jackTokenizer.tokenType() == EnumToken.IDENTIFIER
                || (jackTokenizer.tokenType() == EnumToken.SYMBOL
                        && (jackTokenizer.symbol() == '('
                                || jackTokenizer.symbol() == '['
                                || jackTokenizer.symbol() == '-'
                                || jackTokenizer.symbol() == '~'))
                || (jackTokenizer.tokenType() == EnumToken.KEYWORD
                        && (jackTokenizer.keyWord().equals("true")
                                || jackTokenizer.keyWord().equals("false")
                                || jackTokenizer.keyWord().equals("null")
                                || jackTokenizer.keyWord().equals("this")))) {

            compileExpression();

            numOfExpression++;

            while (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == ',') {
                jackTokenizer.advance();

                compileExpression();

                numOfExpression++;
            }
        }

    }

    private void compileSubroutineCall()
            throws IllegalJackSyntaxException, IllegalTokenException, IOException, SymbolNotFoundException {
        String subroutineName;
        String objectName;

        if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
            throw new IllegalJackSyntaxException("the first word of subroutineCall must be identifier.");
        }
        subroutineName = jackTokenizer.identifier();
        objectName = jackTokenizer.identifier();
        jackTokenizer.advance();

        if (jackTokenizer.tokenType() == EnumToken.SYMBOL && jackTokenizer.symbol() == '.') {
            if (!symbolTable.kindOf(subroutineName).equals("NONE")) {
                subroutineName = symbolTable.typeOf(subroutineName);
            }
            subroutineName = subroutineName + ".";
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != EnumToken.IDENTIFIER) {
                throw new IllegalJackSyntaxException(
                        "subroutine name must be \"identifier\" or \"identifier.identifier\".");
            }
            subroutineName = subroutineName + jackTokenizer.identifier();
            jackTokenizer.advance();

            String kind = symbolTable.kindOf(objectName);

            if (kind.equals("static")) {
                vMwriter.writePush("static", symbolTable.indexOf(objectName));
                numOfExpression = 1;
            } else if (kind.equals("field")) {
                vMwriter.writePush("this", symbolTable.indexOf(objectName));
                numOfExpression = 1;
            } else if (kind.equals("var")) {
                vMwriter.writePush("local", symbolTable.indexOf(objectName));
                numOfExpression = 1;
            } else if (kind.equals("arg")) {
                vMwriter.writePush("argument", symbolTable.indexOf(objectName));
                numOfExpression = 1;
            } else {
                numOfExpression = 0;
            }

        } else {
            subroutineName = className + "." + subroutineName;
            vMwriter.writePush("pointer", 0);
            numOfExpression = 1;
        }

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != '(') {
            throw new IllegalJackSyntaxException("expression list must be surrounded '(' ')'. ");
        }
        jackTokenizer.advance();

        compileExpressionList();

        if (jackTokenizer.tokenType() != EnumToken.SYMBOL || jackTokenizer.symbol() != ')') {
            throw new IllegalJackSyntaxException("expression list must be surrounded '(' ')'. ");
        }
        jackTokenizer.advance();

        vMwriter.writeCall(subroutineName, numOfExpression);
    }

}
