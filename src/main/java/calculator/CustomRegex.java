package calculator;

import java.util.regex.Pattern;


public class CustomRegex {

    public static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");

    public static final Pattern EXPRESSION_CUSTOMIZER = Pattern.compile("//(.)\\n(.+)");

    public static final Pattern TOKEN_PATTERN = Pattern.compile("(-?[1-9][0-9]*)|([,:])");

    public static boolean isDigits(String value) {
        return DIGITS_PATTERN.matcher(value).matches();
    }

}
