import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMwriter {
    private BufferedWriter bufferedWriter;
    
    VMwriter(File outputFile) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
    }

    public void writePush(String segment, int index) throws IOException {
        bufferedWriter.write("push " + segment + " " + String.valueOf(index));
        bufferedWriter.newLine();
    }

    public void writePop(String segment, int index) throws IOException {
        bufferedWriter.write("pop " + segment + " " + String.valueOf(index));
        bufferedWriter.newLine();
    }

    public void writeArithmetic(String command) throws IOException {
        bufferedWriter.write(command);
        bufferedWriter.newLine();
    }

    public void writeLabel(String label) throws IOException {
        bufferedWriter.write("label " + label);
        bufferedWriter.newLine();
    }

    public void writeGoto(String label) throws IOException {
        bufferedWriter.write("goto " + label);
        bufferedWriter.newLine();
    }

    public void writeIf(String label) throws IOException {
        bufferedWriter.write("if-goto " + label);
        bufferedWriter.newLine();
    }

    public void writeCall(String name, int nArgs) throws IOException {
        bufferedWriter.write("call " + name + " " + String.valueOf(nArgs));
        bufferedWriter.newLine();
    }

    public void writeFunction(String name, int nLocals) throws IOException {
        bufferedWriter.write("function " + name + " " + String.valueOf(nLocals));
        bufferedWriter.newLine();
    }

    public void writeReturn() throws IOException {
        bufferedWriter.write("return");
        bufferedWriter.newLine();
    }

    public void close() throws IOException {
        bufferedWriter.close();
    }
}
