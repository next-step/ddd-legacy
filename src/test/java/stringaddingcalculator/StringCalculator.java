package stringaddingcalculator;

import java.util.HashSet;
import java.util.Set;

/**
 * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculator {
    private final Set<StringToNumberParser> stringToNumberParsers = new HashSet<>();

    public StringCalculator(final Set<StringToNumberParser> stringToNumberParsers) {
        if (stringToNumberParsers.isEmpty()) {
            throw new IllegalArgumentException("stringToNumberParsers is required!");
        }
        this.stringToNumberParsers.addAll(stringToNumberParsers);
    }

    public int add(final String source) {
        if (source == null || source.isBlank()) {
            return 0;
        }

        try {
            return stringToNumberParsers.stream()
                    .filter(stringToNumberParser -> stringToNumberParser.isSupport(source))
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    .parse(source)
                    .stream()
                    .peek(this::validateSourceNumber)
                    .reduce(Integer::sum)
                    .orElseThrow(RuntimeException::new);
        } catch (final NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateSourceNumber(final int number) {
        if (number < 0) throw new RuntimeException("number can not be negative");
    }
}
