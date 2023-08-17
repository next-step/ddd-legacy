package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorParserTest {

    private final StringCalculatorParser parser = new StringCalculatorParser();

    @DisplayName("기본 구분자(',' 또는 ':')가 포함된 문자열을 파싱하여 양의 정수 목록을 가져온다")
    @ParameterizedTest
    @ValueSource(strings = {"10,20,30", "10:20:30", "10,20:30"})
    void tc1(String input) {
        PositiveIntegers expected = Stream.of(10, 20, 30)
                .map(PositiveInteger::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), PositiveIntegers::new));
        PositiveIntegers actual = parser.parse(input);

        IntStream.range(0, 3).forEach(index -> assertThat(actual.get(index)).isEqualTo(expected.get(index)));
    }

    @DisplayName("커스텀 구분자로 파싱하여 양의 정수 목록을 가져온다")
    @ParameterizedTest
    @ValueSource(strings = {"//@\n10@20@30"})
    void tc2(String input) {
        PositiveIntegers expected = Stream.of(10, 20, 30)
                .map(PositiveInteger::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), PositiveIntegers::new));
        PositiveIntegers actual = parser.parse(input);

        IntStream.range(0, 3).forEach(index -> assertThat(actual.get(index)).isEqualTo(expected.get(index)));
    }
}