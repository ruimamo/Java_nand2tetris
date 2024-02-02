import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class JackTokenizer {
    private BufferedReader bufferedReader;
    private char currentReaderChar;

    private String currentToken;
    private String nextToken;
    private boolean hasNext = true;

    JackTokenizer(File file) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            currentReaderChar = '\s';
            try {
                readNextToken();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public void close() {
        try {
            this.bufferedReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void readNextChar() throws IOException {
        if (!bufferedReader.ready()) {
            this.hasNext = false;
        } else {
            this.currentReaderChar = (char) bufferedReader.read();
        }
    }

    private void skipBlank() {
        while (this.currentReaderChar == '\s'
                || this.currentReaderChar == '\r'
                || this.currentReaderChar == '\n'
                || this.currentReaderChar == '\t') {
            try {
                readNextChar();
                if (this.hasNext == false) {
                    break;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void readNextToken() throws IOException {
        char chr;
        String nextElement;
        boolean isInCommentOut = false;
        boolean willBeOutOfCommentOut = false;

        chr = currentReaderChar;

        do {
            nextElement = "";
            if (willBeOutOfCommentOut) {
                isInCommentOut = false;
                willBeOutOfCommentOut = false;
            }

            skipBlank();

            chr = this.currentReaderChar;

            if (chr == '{' || chr == '}' || chr == '(' || chr == ')' || chr == '[' || chr == ']'
                    || chr == '.' || chr == ',' || chr == ';'
                    || chr == '+' || chr == '-' || chr == '&'
                    || chr == '|' || chr == '<' || chr == '>' || chr == '=' || chr == '~') {
                nextElement += (char) chr;
                readNextChar();
            } else if (chr == '*') {
                nextElement += '*';
                readNextChar();
                if (this.currentReaderChar == '/') {
                    nextElement += '/';
                    // 次のトークンからコメントの外になる
                    willBeOutOfCommentOut = true;
                    readNextChar();
                }
            } else if (chr == '/') {
                nextElement += '/';
                readNextChar();
                if (this.currentReaderChar == '/') {
                    nextElement += '/';
                    // この行はコメントになる。次のトークンからコメントの外になる
                    bufferedReader.readLine();
                    isInCommentOut = true;
                    willBeOutOfCommentOut = true;
                    readNextChar();
                } else if (this.currentReaderChar == '*') {
                    nextElement += '*';
                    readNextChar();
                    if (this.currentReaderChar == '*') {
                        nextElement += '*';
                        // このトークンからコメントに入る
                        isInCommentOut = true;
                        readNextChar();
                    } else {
                        // このトークンからコメントに入る
                        isInCommentOut = true;
                    }
                }
            } else if (chr == '"') {
                nextElement += '"';
                readNextChar();
                while (this.currentReaderChar != '"') {
                    nextElement += (char) this.currentReaderChar;
                    readNextChar();
                }
                nextElement += (char) this.currentReaderChar;
                readNextChar();
            } else {
                while (chr != '{' && chr != '}' && chr != '(' && chr != ')' && chr != '[' && chr != ']'
                        && chr != '.' && chr != ',' && chr != ';'
                        && chr != '+' && chr != '-' && chr != '*' && chr != '/' && chr != '&'
                        && chr != '|' && chr != '<' && chr != '>' && chr != '=' && chr != '~' && chr != '\s'
                        && chr != '"') {
                    nextElement += (char) chr;
                    if (!this.hasNext) {
                        break;
                    }
                    chr = (char) bufferedReader.read();
                }
                // 現トークン確定
                this.currentReaderChar = chr;
            }

        } while (isInCommentOut && this.hasNext);

        this.nextToken = nextElement;
    }

    public boolean hasMoreTokens() {
        return this.hasNext;
    }

    public void advance() {
        this.currentToken = this.nextToken;
        System.out.println(this.currentToken);
        try {
            readNextToken();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public EnumToken tokenType() throws IllegalTokenException {
        String keywordPattern = "^(class|constructor|function|method|field|static|var"
                + "|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)$";
        String symbolPattern = "^(\\{|\\}|\\(|\\)|\\[|\\]|\\.|,|;|\\+|-|\\*|\\/|&|\\||<|>|=|~)$";
        String intConstPattern = "^(0|[1-9][0-9]*)$";
        String strConstPattern = "^\".*\"$";
        String identifierPattern = "[a-zA-Z\\_][a-zA-Z0-9\\_]*";

        Pattern keywordp = Pattern.compile(keywordPattern);
        Pattern symbolp = Pattern.compile(symbolPattern);
        Pattern intconstp = Pattern.compile(intConstPattern);
        Pattern strconstp = Pattern.compile(strConstPattern);
        Pattern identifierp = Pattern.compile(identifierPattern);

        if (keywordp.matcher(currentToken).matches()) {
            return EnumToken.KEYWORD;
        } else if (symbolp.matcher(currentToken).matches()) {
            return EnumToken.SYMBOL;
        } else if (intconstp.matcher(currentToken).matches()) {
            if (0 <= Integer.parseInt(currentToken) && Integer.parseInt(currentToken) <= 32767) {
                return EnumToken.INT_CONST;
            } else {
                throw new IllegalTokenException(currentToken + " is not 0 <= n <= 32767");
            }
        } else if (strconstp.matcher(currentToken).matches()) {
            return EnumToken.STRING_CONST;
        } else if (identifierp.matcher(currentToken).matches()) {
            return EnumToken.IDENTIFIER;
        } else {
            throw new IllegalTokenException(currentToken + " is illegal token.");
        }
    }

    public String keyWord() {
        return currentToken;
    }

    public char symbol() {
        return currentToken.toCharArray()[0];
    }

    public String identifier() {
        return currentToken;
    }

    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    public String stringVal() {
        return currentToken.replace("\"", "")
                .replace("\r", "")
                .replace("\n", "");
    }
}
