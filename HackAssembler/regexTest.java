import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regexTest {
    public static void main(String[] args){
        Pattern p = Pattern.compile("^\s*\\([a-zA-Z\\_\\.\\$\\:][a-zA-Z0-9\\_\\.\\$\\:]*\\)\s*$");
        Matcher m = p.matcher("(TH:$._");
        System.out.println(m.matches());
        Matcher m2 = p.matcher("3Threes");
        System.out.println(m2.matches());
        Matcher m3 = p.matcher("   (TH:$._) ");
        System.out.println(m3.matches());
        Matcher m4 = p.matcher(" TH: $._ ");
        System.out.println(m4.matches());

        String destPattern = "(A?M?D?=)?";
        Pattern p2 = Pattern.compile(destPattern);
        Matcher m5 = p2.matcher("AMD=");
        System.out.println(m5.matches());
        Matcher m6 = p2.matcher("");
        System.out.println(m6.matches());
        Matcher m7 = p2.matcher("DM=");
        System.out.println(m7.matches());

        String compPattern = "(0|1|-1|[DAM]|![DAM]|-[DAM]|[DAM]\\+1|[DAM]-1|D\\+[AM]|D-[AM]|[AM]-D|D&[AM]|D\\|[AM])";
        Pattern p3 = Pattern.compile(compPattern);
        Matcher m8 = p3.matcher("D|A");
        System.out.println(m8.matches());
        Matcher m9 = p3.matcher("M-D");
        System.out.println(m9.matches());
        Matcher m10 = p3.matcher("-1");
        System.out.println(m10.matches());
        Matcher m11 = p3.matcher("A-M");
        System.out.println(m11.matches());
        Matcher m12 = p3.matcher("D+1");
        System.out.println(m12.matches());

        System.out.println("file: " + "MaxL.asm".matches("^.*\\.asm$"));
    }
}
