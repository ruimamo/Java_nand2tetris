import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeWriter {
    private BufferedWriter bufferedWriter;
    private String currentFileName;
    private int lineCount;
    private String currentFunctionName;
    private int returnAddressCount;

    CodeWriter(File inputFile) {

        try {
            String outputFilePath;
            if (Files.isDirectory(Paths.get(inputFile.getPath()))) {
                outputFilePath = inputFile.getPath() + ".asm";
            } else {
                outputFilePath = inputFile.getPath().substring(0, inputFile.getPath().length() - 3) + ".asm";
            }
            File outputFile = new File(outputFilePath);
            bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lineCount = 0;
        currentFunctionName = "";
        returnAddressCount = 0;
        writeInit();
    }

    public void setFileName(File inputFile) {
        String inputFileName = inputFile.getName();
        if (!inputFileName.matches("^.*\\.vm$")) {
            try {
                throw new IOException(inputFile.getPath() + " is not .vm file");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        currentFileName = inputFileName.substring(0, inputFileName.length() - 3);
        currentFunctionName = "";
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeln(String asm) {
        try {
            bufferedWriter.write(asm);
            bufferedWriter.newLine();
            lineCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writelnWithoutCount(String asm) {
        try {
            bufferedWriter.write(asm);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {
        if (command.equals("add")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("M=D+M");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("sub")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("M=M-D");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("neg")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("M=-M");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("eq")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M-D");
            writeln("@" + (lineCount + 7));
            writeln("D;JEQ");
            writeln("@SP");
            writeln("A=M");
            writeln("M=0");
            writeln("@" + (lineCount + 5));
            writeln("0;JMP");
            writeln("@SP");
            writeln("A=M");
            writeln("M=-1");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("gt")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M-D");
            writeln("@" + (lineCount + 7));
            writeln("D;JGT");
            writeln("@SP");
            writeln("A=M");
            writeln("M=0");
            writeln("@" + (lineCount + 5));
            writeln("0;JMP");
            writeln("@SP");
            writeln("A=M");
            writeln("M=-1");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("lt")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M-D");
            writeln("@" + (lineCount + 7));
            writeln("D;JLT");
            writeln("@SP");
            writeln("A=M");
            writeln("M=0");
            writeln("@" + (lineCount + 5));
            writeln("0;JMP");
            writeln("@SP");
            writeln("A=M");
            writeln("M=-1");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("and")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("M=D&M");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("or")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("D=M");
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("M=D|M");
            writeln("@SP");
            writeln("M=M+1");
        } else if (command.equals("not")) {
            writeln("@SP");
            writeln("M=M-1");
            writeln("A=M");
            writeln("M=!M");
            writeln("@SP");
            writeln("M=M+1");
        }
    }

    public void writePushPop(String command, String segment, int index) {
        if (command.equals("push")) {
            if (segment.equals("local")) {
                writeln("@LCL");
                writeln("D=M");
                writeln("@" + index);
                writeln("A=D+A");
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("argument")) {
                writeln("@ARG");
                writeln("D=M");
                writeln("@" + index);
                writeln("A=D+A");
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("this")) {
                writeln("@THIS");
                writeln("D=M");
                writeln("@" + index);
                writeln("A=D+A");
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("that")) {
                writeln("@THAT");
                writeln("D=M");
                writeln("@" + index);
                writeln("A=D+A");
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("pointer")) {
                writeln("@3");
                writeln("D=A");
                writeln("@" + index);
                writeln("A=D+A");
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("temp")) {
                writeln("@5");
                writeln("D=A");
                writeln("@" + index);
                writeln("A=D+A");
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("constant")) {
                writeln("@" + index);
                writeln("D=A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            } else if (segment.equals("static")) {
                writeln("@" + currentFileName + "." + index);
                writeln("D=M");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M+1");
            }
        } else if (command.equals("pop")) {
            if (segment.equals("local")) {
                writeln("@LCL");
                writeln("D=M");
                writeln("@" + index);
                writeln("D=D+A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            } else if (segment.equals("argument")) {
                writeln("@ARG");
                writeln("D=M");
                writeln("@" + index);
                writeln("D=D+A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            } else if (segment.equals("this")) {
                writeln("@THIS");
                writeln("D=M");
                writeln("@" + index);
                writeln("D=D+A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            } else if (segment.equals("that")) {
                writeln("@THAT");
                writeln("D=M");
                writeln("@" + index);
                writeln("D=D+A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            } else if (segment.equals("pointer")) {
                writeln("@3");
                writeln("D=A");
                writeln("@" + index);
                writeln("D=D+A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            } else if (segment.equals("temp")) {
                writeln("@5");
                writeln("D=A");
                writeln("@" + index);
                writeln("D=D+A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            } else if (segment.equals("constant")) {
                writeln("@SP");
                writeln("M=M-1");
                writeln("A=M");
                writeln("D=M");
                writeln("@" + index);
                writeln("M=D");
            } else if (segment.equals("static")) {
                writeln("@" + currentFileName + "." + index);
                writeln("D=A");
                writeln("@SP");
                writeln("A=M");
                writeln("M=D");
                writeln("A=A-1");
                writeln("D=M");
                writeln("A=A+1");
                writeln("A=M");
                writeln("M=D");
                writeln("@SP");
                writeln("M=M-1");
            }
        }
    }

    public void writeLabel(String label) {
        writelnWithoutCount("(" + currentFunctionName + "$" + label + ")");
    }

    public void writeGoto(String label) {
        writeln("@" + currentFunctionName + "$" + label);
        writeln("0;JMP");
    }

    public void writeIf(String label) {
        writeln("@SP");
        writeln("M=M-1");
        writeln("A=M");
        writeln("D=M");
        writeln("@" + currentFunctionName + "$" + label);
        writeln("D;JNE");
    }

    public void writeCall(String functionName, int numArgs) {
        writeln("@RET_ADDRESS" + returnAddressCount);
        writeln("D=A");
        writeln("@SP");
        writeln("A=M");
        writeln("M=D");
        writeln("@SP");
        writeln("M=M+1");

        writeln("@LCL");
        writeln("D=M");
        writeln("@SP");
        writeln("A=M");
        writeln("M=D");
        writeln("@SP");
        writeln("M=M+1");

        writeln("@ARG");
        writeln("D=M");
        writeln("@SP");
        writeln("A=M");
        writeln("M=D");
        writeln("@SP");
        writeln("M=M+1");

        writeln("@THIS");
        writeln("D=M");
        writeln("@SP");
        writeln("A=M");
        writeln("M=D");
        writeln("@SP");
        writeln("M=M+1");

        writeln("@THAT");
        writeln("D=M");
        writeln("@SP");
        writeln("A=M");
        writeln("M=D");
        writeln("@SP");
        writeln("M=M+1");

        writeln("@SP");
        writeln("D=M");
        writeln("@" + (numArgs + 5));
        writeln("D=D-A");
        writeln("@ARG");
        writeln("M=D");

        writeln("@SP");
        writeln("D=M");
        writeln("@LCL");
        writeln("M=D");

        writeln("@" + functionName);
        writeln("0;JMP");

        writelnWithoutCount("(RET_ADDRESS" + returnAddressCount + ")");

        returnAddressCount++;
    }

    public void writeReturn() {
        writeln("@LCL");
        writeln("D=M");
        writeln("@13");
        writeln("M=D");
        writeln("@5");
        writeln("A=D-A");
        writeln("D=M");
        writeln("@14");
        writeln("M=D");

        writeln("@SP");
        writeln("A=M-1");
        writeln("D=M");
        writeln("@ARG");
        writeln("A=M");
        writeln("M=D");

        writeln("@ARG");
        writeln("D=M+1");
        writeln("@SP");
        writeln("M=D");

        writeln("@13");
        writeln("M=M-1");
        writeln("A=M");
        writeln("D=M");
        writeln("@THAT");
        writeln("M=D");

        writeln("@13");
        writeln("M=M-1");
        writeln("A=M");
        writeln("D=M");
        writeln("@THIS");
        writeln("M=D");

        writeln("@13");
        writeln("M=M-1");
        writeln("A=M");
        writeln("D=M");
        writeln("@ARG");
        writeln("M=D");

        writeln("@13");
        writeln("M=M-1");
        writeln("A=M");
        writeln("D=M");
        writeln("@LCL");
        writeln("M=D");

        writeln("@14");
        writeln("A=M");
        writeln("0;JMP");
    }

    public void writeFunction(String functionName, int numLocals) {
        writelnWithoutCount("(" + functionName + ")");
        for (int i = 0; i < numLocals; i++) {
            writePushPop("push", "constant", 0);
        }
        currentFunctionName = functionName;
    }

    public void writeInit() {
        writeln("@256");
        writeln("D=A");
        writeln("@SP");
        writeln("M=D");
        writeCall("Sys.init", 0);
    }
}
