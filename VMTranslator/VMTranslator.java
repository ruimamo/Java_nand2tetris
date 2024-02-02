import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VMTranslator {
    public static void main(String[] args) {

        Path p = Paths.get(args[0]);

        Parser parser;
        CodeWriter codeWriter;

        if (Files.isDirectory(p)) {
            File dir = new File(args[0]);
            File[] files = dir.listFiles();

            codeWriter = new CodeWriter(dir);

            for (int i = 0; i < files.length; i++) {
                System.out.println("getName: " + files[i].getName());
                if (files[i].getName().matches("^.*\\.vm$")) {
                    parser = new Parser(files[i]);
                    codeWriter.setFileName(files[i]);
                    translateOneFile(parser, codeWriter);
                }
            }

        } else {
            parser = new Parser(new File(args[0]));
            codeWriter = new CodeWriter(new File(args[0]));
            translateOneFile(parser, codeWriter);
        }

        codeWriter.close();

    }

    private static void translateOneFile(Parser parser, CodeWriter codeWriter) {

        try {

            while (parser.hasMoreCommands()) {
                parser.advance();

                if (parser.currentType() == EnumCommands.C_ARITHMETIC) {
                    codeWriter.writeArithmetic(parser.arg1());
                } else if (parser.currentType() == EnumCommands.C_PUSH) {
                    codeWriter.writePushPop("push", parser.arg1(), parser.arg2());
                } else if (parser.currentType() == EnumCommands.C_POP) {
                    codeWriter.writePushPop("pop", parser.arg1(), parser.arg2());
                } else if (parser.currentType() == EnumCommands.C_LABEL) {
                    codeWriter.writeLabel(parser.arg1());
                } else if (parser.currentType() == EnumCommands.C_GOTO) {
                    codeWriter.writeGoto(parser.arg1());
                } else if (parser.currentType() == EnumCommands.C_IF) {
                    codeWriter.writeIf(parser.arg1());
                } else if (parser.currentType() == EnumCommands.C_FUNCTION) {
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                } else if (parser.currentType() == EnumCommands.C_RETURN) {
                    codeWriter.writeReturn();
                } else if (parser.currentType() == EnumCommands.C_CALL) {
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                }
            }

            parser.closeBufferedReader();
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }
    }

}
