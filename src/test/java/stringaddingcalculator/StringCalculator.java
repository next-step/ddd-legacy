package stringaddingcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculator {
    private static final Pattern SIMPLE_NUMBER_FORMAT_STRING_PATTERN = Pattern.compile("^([0-9]+)$");
    private static final Pattern CUSTOM_SEPARATOR_FORMAT_STRING_PATTERN = Pattern.compile("//(.)\\\\n(.+)");
    private static final String DEFAULT_SEPARATOR_REGULAR_EXPRESSION = "[,:]";
    private static final String COLON_SEPARATOR = ":";

    public int add(final String source) {
        if (source == null || source.isBlank()) {
            return 0;
        }

        final Matcher simpleNumberFormatStringMatcher = SIMPLE_NUMBER_FORMAT_STRING_PATTERN.matcher(source);
        if (simpleNumberFormatStringMatcher.find()) {
            return parseIntAndValidate(source);
        }

        final Matcher matcher = CUSTOM_SEPARATOR_FORMAT_STRING_PATTERN.matcher(source);
        if (matcher.find()) {
            final String replacedSeparatorSource = matcher.group(2).replace(matcher.group(1), COLON_SEPARATOR);
            return add(COLON_SEPARATOR, replacedSeparatorSource);
        }

        return add(DEFAULT_SEPARATOR_REGULAR_EXPRESSION, source);
    }

    private int add(final String separatorRegEx, final String source) {
        if (separatorRegEx == null || separatorRegEx.isBlank()) {
            throw new IllegalArgumentException("separator can not be null ");
        }
        return Arrays.stream(source.split(separatorRegEx))
                .mapToInt(this::parseIntAndValidate)
                .sum();
    }

    private int parseIntAndValidate(final String source) {
        try {
            final int sourceNumber = Integer.parseInt(source);
            validateSourceNumber(sourceNumber);
            return sourceNumber;
        } catch (final NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateSourceNumber(final int number) {
        if (number < 0) throw new RuntimeException("number can not be negative");
    }
}
