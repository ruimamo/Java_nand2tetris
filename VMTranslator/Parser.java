import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.io.BufferedReader;

public class Parser {
    private BufferedReader bufferedReader;
    private String currentCommand;
    private String nextCommand;
    private boolean hasLine = true;

    Parser(File file) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            readNextLineAndTrim();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public void closeBufferedReader() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void readNextLineAndTrim() {
        String nextLine = "";

        try {
            while (nextLine.equals("")) {
                if (bufferedReader.ready() == false) {
                    hasLine = false;
                    break;
                }
                nextLine = bufferedReader.readLine();
                int commentIndex = nextLine.indexOf("//");
                if (commentIndex != -1) {
                    nextLine = nextLine.substring(0, commentIndex);
                }
                nextLine = nextLine.trim();
            }

            this.nextCommand = nextLine;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean hasMoreCommands() {
        return this.hasLine;
    }

    public void advance() {
        if (this.hasLine) {
            this.currentCommand = this.nextCommand;
            readNextLineAndTrim();
        } else {
            System.out.println("there is no more commands!");
        }
    }

    public EnumCommands currentType() throws IllegalCommandException {
        String arithmeticPattern = "^(add|sub|neg|eq|gt|lt|and|or|not)$";
        String segmentPattern = "(argument|local|static|constant|this|that|pointer|temp)";
        String symbolPattern = "[a-zA-Z\\_\\.\\$\\:][a-zA-Z0-9\\_\\.\\$\\:]*";
        String naturalNumberPattern = "[0-9]+";

        Pattern arithp = Pattern.compile(arithmeticPattern);
        Pattern pushp = Pattern.compile("^push\s+" + segmentPattern + "\s+" + naturalNumberPattern);
        Pattern popp = Pattern.compile("^pop\s+" + segmentPattern + "\s+" + naturalNumberPattern);
        Pattern labelp = Pattern.compile("^label\s+" + symbolPattern + "$");
        Pattern gotop = Pattern.compile("^goto\s+" + symbolPattern + "$");
        Pattern ifp = Pattern.compile("^if-goto\s+" + symbolPattern + "$");
        Pattern functionp = Pattern.compile("^function\s+" + symbolPattern + "\s+" + naturalNumberPattern + "$");
        Pattern callp = Pattern.compile("^call\s+" + symbolPattern + "\s+" + naturalNumberPattern + "$");
        Pattern returnp = Pattern.compile("^return$");

        if (arithp.matcher(currentCommand).matches()) {
            return EnumCommands.C_ARITHMETIC;
        } else if (pushp.matcher(currentCommand).matches()) {
            return EnumCommands.C_PUSH;
        } else if (popp.matcher(currentCommand).matches()) {
            return EnumCommands.C_POP;
        } else if (labelp.matcher(currentCommand).matches()) {
            return EnumCommands.C_LABEL;
        } else if (gotop.matcher(currentCommand).matches()) {
            return EnumCommands.C_GOTO;
        } else if (ifp.matcher(currentCommand).matches()) {
            return EnumCommands.C_IF;
        } else if (functionp.matcher(currentCommand).matches()) {
            return EnumCommands.C_FUNCTION;
        } else if (callp.matcher(currentCommand).matches()) {
            return EnumCommands.C_CALL;
        } else if (returnp.matcher(currentCommand).matches()) {
            return EnumCommands.C_RETURN;
        } else {
            throw new IllegalCommandException(currentCommand + " is illegal command.");
        }
    }

    public String arg1() {
        String firstArg = currentCommand;

        try {
            if (currentType() == EnumCommands.C_ARITHMETIC) {
                return firstArg;
            } else {
                firstArg = firstArg.substring(firstArg.indexOf(" ") + 1).trim();
                int secondSpaceIdx = firstArg.indexOf(" ");
                if (secondSpaceIdx != -1) {
                    firstArg = firstArg.substring(0, secondSpaceIdx).trim();
                }

                return firstArg;
            }
        } catch (IllegalCommandException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int arg2() {
        String secondArg = currentCommand;

        secondArg = secondArg.substring(secondArg.indexOf(" ") + 1).trim();
        secondArg = secondArg.substring(secondArg.indexOf(" ") + 1).trim();
        return Integer.parseInt(secondArg);
    }
}
