package stringcalcurator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
    private static final String DEFAULT_SPLIT_REGEX = "[,:]";
    private static final String CUSTOM_SPLIT_REGEX = "//(.)\n(.*)";

    public static String compileSeparator(String text){
        Matcher matcher = Pattern.compile(CUSTOM_SPLIT_REGEX).matcher(text);
        if(matcher.find()){
            return matcher.group(1);
        }
        return DEFAULT_SPLIT_REGEX;
    }

    public static String removeIfCustomSeparator(String text){
        Matcher matcher = Pattern.compile(CUSTOM_SPLIT_REGEX).matcher(text);
        if(matcher.find()){
            return matcher.group(2);
        }
        return text;
    }
}
