import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JackCompiler {
    public static void main(String[] args) {
        Path p = Paths.get(args[0]);

        if (Files.isDirectory(p)) {
            File dir = new File(args[0]);
            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().matches("^.*\\.jack$")) {
                    compileOneFile(files[i]);
                }
            }
        } else {
            File file = new File(args[0]);

            compileOneFile(file);
        }
    }

    private static void compileOneFile(File inputFile) {
        CompilationEngine compilationEngine = new CompilationEngine(inputFile);

        try {
            compilationEngine.compileFile();
        } catch (IllegalTokenException | IllegalJackSyntaxException | IOException | IllegalSymbolTableException | SymbolNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            compilationEngine.closeInputOutputFile();
        }
    }
}
