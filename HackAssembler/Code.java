public class Code {
    public String dest(String mnemonic) {
        String result3bit = "";
        if (mnemonic.equals("null")) {
            return "000";
        }
        if (mnemonic.indexOf("A") != -1) {
            result3bit += "1";
        } else {
            result3bit += "0";
        }
        if (mnemonic.indexOf("D") != -1) {
            result3bit += "1";
        } else {
            result3bit += "0";
        }
        if (mnemonic.indexOf("M") != -1) {
            result3bit += "1";
        } else {
            result3bit += "0";
        }
        return result3bit;
    }

    public String comp(String mnemonic) {
        String result7bit = "";

        if (mnemonic.indexOf("M") != -1) {
            result7bit += "1";
        } else {
            result7bit += "0";
        }

        if (mnemonic.equals("0")) {
            result7bit += "101010";
        } else if (mnemonic.equals("1")) {
            result7bit += "111111";
        } else if (mnemonic.equals("-1")) {
            result7bit += "111010";
        } else if (mnemonic.equals("D")) {
            result7bit += "001100";
        } else if (mnemonic.equals("A") || mnemonic.equals("M")) {
            result7bit += "110000";
        } else if (mnemonic.equals("!D")) {
            result7bit += "001101";
        } else if (mnemonic.equals("!A") || mnemonic.equals("!M")) {
            result7bit += "110001";
        } else if (mnemonic.equals("-D")) {
            result7bit += "001111";
        } else if (mnemonic.equals("-A") || mnemonic.equals("-M")) {
            result7bit += "110011";
        } else if (mnemonic.equals("D+1") || mnemonic.equals("1+D")) {
            result7bit += "011111";
        } else if (mnemonic.equals("A+1") || mnemonic.equals("M+1")
                || mnemonic.equals("1+A") || mnemonic.equals("1+M")) {
            result7bit += "110111";
        } else if (mnemonic.equals("D-1")) {
            result7bit += "001110";
        } else if (mnemonic.equals("A-1") || mnemonic.equals("M-1")) {
            result7bit += "110010";
        } else if (mnemonic.equals("D+A") || mnemonic.equals("D+M")
                || mnemonic.equals("A+D") || mnemonic.equals("M+D")) {
            result7bit += "000010";
        } else if (mnemonic.equals("D-A") || mnemonic.equals("D-M")) {
            result7bit += "010011";
        } else if (mnemonic.equals("A-D") || mnemonic.equals("M-D")) {
            result7bit += "000111";
        } else if (mnemonic.equals("D&A") || mnemonic.equals("D&M")
                || mnemonic.equals("A&D") || mnemonic.equals("M&D")) {
            result7bit += "000000";
        } else if (mnemonic.equals("D|A") || mnemonic.equals("D|M")
                || mnemonic.equals("A|D") || mnemonic.equals("M|D")) {
            result7bit += "010101";
        }

        return result7bit;
    }

    public String jump(String mnemonic) {
        if (mnemonic.equals("null")) {
            return "000";
        } else if (mnemonic.equals("JGT")) {
            return "001";
        } else if (mnemonic.equals("JEQ")) {
            return "010";
        } else if (mnemonic.equals("JGE")) {
            return "011";
        } else if (mnemonic.equals("JLT")) {
            return "100";
        } else if (mnemonic.equals("JNE")) {
            return "101";
        } else if (mnemonic.equals("JLE")) {
            return "110";
        } else if (mnemonic.equals("JMP")) {
            return "111";
        }

        return "";
    }
}
