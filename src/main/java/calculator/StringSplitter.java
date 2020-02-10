package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {
    private static final String DELIMITER_REGEX = ",|:";
    private static final String CUSTOMED_DELIMITER_MATCHING_REGEX = "//(.)\n(.*)";
    private static final Pattern CUSTOM_SPLIT_REGEX = Pattern.compile(CUSTOMED_DELIMITER_MATCHING_REGEX);

    public static String[] extractNumberStringByDelimiter(String text){

        Matcher m = CUSTOM_SPLIT_REGEX.matcher(text);

        if(m.matches()){
            String delimiter = m.group(1);
            String numberString = m.group(2);

            return numberString.split(delimiter);
        }

        return text.split(DELIMITER_REGEX);
    }

}
