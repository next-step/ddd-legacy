package calculator;

import java.util.regex.Pattern;


public class CustomRegex {

    static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");

    static final Pattern CUSTOM_USER_SETTING = Pattern.compile("//(.)\\n(.+)");

    public static boolean isDigits(String value) {
        return DIGITS_PATTERN.matcher(value).matches();
    }

}
