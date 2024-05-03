package stringCalculator;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberConvertor {
    public static final int REGEX_INDEX = 1;
    public static final int NUMSTRING_INDEX = 2;
    private final static Pattern customPattern = Pattern.compile("//(.)\\n(.*)");
    private final static Pattern defaultPattern = Pattern.compile("[,\\:]");
    private final String[] numbers;

    public NumberConvertor(String text) {
        this.validation(text);

        this.numbers = this.splitTextByRegex(text);
    }

    public String[] getNumbers() {
        return numbers;
    }

    private void validation(String text){
        if(Optional.ofNullable(text).isEmpty() || text.isBlank()){
            throw new IllegalArgumentException("input text is empty");
        }
    }

    private String[] splitTextByRegex(String text) {
        Matcher matcher = customPattern.matcher(text);

        if (matcher.find()) {
            String regex = matcher.group(REGEX_INDEX);
            String tempString = matcher.group(NUMSTRING_INDEX).replaceAll("\n", "");

            return tempString.split(regex);
        }

        return defaultPattern.split(text);
    }
}
