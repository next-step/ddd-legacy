package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Splitter {
    private String text;
    private String[] textArray;
    private static final String TEXT_SPERATOR_1 = ",";
    private static final String TEXT_SPERATOR_2 = ":";

    public Splitter(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if(m.find()) {
            String customDelimiter = m.group(1);
            this.textArray = m.group(2).split(customDelimiter);
        } else
            this.textArray = text.split(String.format("%s|%s", TEXT_SPERATOR_1, TEXT_SPERATOR_2));
    }

    public String[] getSplitText() {
        for(String o : textArray) {
            System.out.println(o);
        }
        return this.textArray;
    }
}
