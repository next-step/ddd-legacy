package stringCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberConvertor {
    public static final int SEPARATOR_INDEX = 1;
    public static final int NUMSTRING_INDEX = 2;
    private final static Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\\n(.*)");
    private final static Pattern DEFAULT_PATTERN = Pattern.compile("[,\\:]");

    private NumberConvertor() {
    }

    public static List<PositiveNumber> getNumbers(String text) {
        validation(text);

        String[] numbers = splitTextByRegex(text);

        return Arrays.stream(numbers)
                .map(PositiveNumber::from)
                .collect(Collectors.toList());
    }

    private static void validation(String text){
        if(text == null || text.isBlank()){
            throw new IllegalArgumentException("input text is empty");
        }
    }

    private static String[] splitTextByRegex(String text) {
        Matcher matcher = CUSTOM_PATTERN.matcher(text);

        if (matcher.find()) {
            String separator = matcher.group(SEPARATOR_INDEX);
            String tempString = matcher.group(NUMSTRING_INDEX).replaceAll("\n", "");

            return tempString.split(separator);
        }

        return DEFAULT_PATTERN.split(text);
    }
}
