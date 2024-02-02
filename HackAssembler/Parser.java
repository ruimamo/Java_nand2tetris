import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    BufferedReader bufferedReader;
    String currentCommand;

    Parser(File file) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public boolean hasMoreCommands() {
        try {
            return bufferedReader.ready();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void advance() {
        try {
            do {
                if (hasMoreCommands()) {
                    String currentLine = bufferedReader.readLine();
                    int commentIndex = currentLine.indexOf("//");
                    if (commentIndex != -1) {
                        currentLine = currentLine.substring(0, commentIndex);
                    }
                    currentCommand = currentLine.trim();
                } else {
                    break;
                }
            } while (currentCommand.equals("")); // コメントだけの行や空行なら次の行を読む
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public EnumCommands commandType() throws IllegalCommandException {
        String destPattern = "(A?M?D?=)?";
        String compPattern = "(0|1|-1|[DAM]|![DAM]|-[DAM]|[DAM]\\+1|[DAM]-1|D\\+[AM]|D-[AM]|[AM]-D|D&[AM]|D\\|[AM])";
        String jumpPattern = "(;(JGT|JEQ|JGE|JLT|JNE|JLE|JMP))?";

        String symbolPattern = "([a-zA-Z\\_\\.\\$\\:][a-zA-Z0-9\\_\\.\\$\\:]*|[0-9]*)";

        Pattern ap = Pattern.compile("^\s*@" + symbolPattern + "\s*$");
        Pattern cp = Pattern.compile("^\s*" + destPattern + compPattern + jumpPattern + "\s*$");
        Pattern lp = Pattern.compile("^\s*\\(" + symbolPattern + "\\)\s*$");

        Matcher am = ap.matcher(currentCommand);
        Matcher cm = cp.matcher(currentCommand);
        Matcher lm = lp.matcher(currentCommand);

        if (am.matches()) {
            return EnumCommands.A_COMMAND;
        } else if (cm.matches()) {
            return EnumCommands.C_COMMAND;
        } else if (lm.matches()) {
            return EnumCommands.L_COMMAND;
        } else {
            throw new IllegalCommandException("include a illegal command.");
        }
    }

    public String symbol() throws IllegalCommandException {
        if (commandType() == EnumCommands.A_COMMAND) {
            return currentCommand.substring(1);
        } else if (commandType() == EnumCommands.L_COMMAND) {
            return currentCommand.substring(1, currentCommand.length() - 1);
        } else {
            throw new IllegalCommandException("symbol() should be called"
                    + "when currentCommand is A_COMMAND or L_COMMAND");
        }
    }

    public String dest() throws IllegalCommandException {
        if (commandType() == EnumCommands.C_COMMAND) {
            int equalIdx = currentCommand.indexOf("=");
            if (equalIdx != -1) {
                return currentCommand.substring(0, equalIdx);
            } else {
                return "null";
            }
        } else {
            throw new IllegalCommandException("symbol() should be called"
                    + "when currentCommand is C_COMMAND");
        }
    }

    public String comp() throws IllegalCommandException {
        if (commandType() == EnumCommands.C_COMMAND) {
            String compCommand = currentCommand;
            int equalIdx = currentCommand.indexOf("=");
            int semicolonIdx = currentCommand.indexOf(";");
            if (semicolonIdx != -1) {
                compCommand = compCommand.substring(0, semicolonIdx);
            }
            if (equalIdx != -1) {
                compCommand = compCommand.substring(equalIdx + 1);
            }
            return compCommand;
        } else {
            throw new IllegalCommandException("symbol() should be called"
                    + "when currentCommand is C_COMMAND");
        }
    }

    public String jump() throws IllegalCommandException {
        if (commandType() == EnumCommands.C_COMMAND) {
            int semicolonIdx = currentCommand.indexOf(";");
            if (semicolonIdx != -1) {
                return currentCommand.substring(semicolonIdx + 1);
            } else {
                return "null";
            }
        } else {
            throw new IllegalCommandException("symbol() should be called"
                    + "when currentCommand is C_COMMAND");
        }
    }

    public void closeBufferedReader() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
