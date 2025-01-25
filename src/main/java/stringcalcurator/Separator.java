package stringcalcurator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
    private static final String DEFAULT_SPLIT_REGEX = "[,:]";
    private static final String CUSTOM_SPLIT_REGEX = "//(.)\n(.*)";

    public static String[] splitNumber(String text){
        Matcher matcher = Pattern.compile(CUSTOM_SPLIT_REGEX).matcher(text);
        String separator = DEFAULT_SPLIT_REGEX;
        if(matcher.find()){
            separator = matcher.group(1);
            text = matcher.group(2);
        }
        return text.split(separator);
    }
}
