package calculator;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TextConverterTest {

    private TextConverter<Integer> converter;

    @BeforeEach
    void init() {
        converter = new TextConverter<>(Integer::parseInt);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    @DisplayName("구분자로 분리된 문자열을 정수 리스트로 변환한다")
    void convertToList(String text, String delimiter, List<Integer> expected) {
        // when
        List<Integer> result = converter.convertToList(text, delimiter);

        // then
        assertThat(result).containsExactlyElementsOf(expected);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("1,2,3", ",", List.of(1, 2, 3)),
                Arguments.of("1:2:3", ":", List.of(1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource("provideSpecialDelimiterCases")
    @DisplayName("특수문자를 구분자로 사용할 수 있다")
    void convertToList_with_special_delimiter(String text, String delimiter, List<Integer> expected) {
        // when
        List<Integer> result = converter.convertToList(text, delimiter);

        // then
        assertThat(result).containsExactlyElementsOf(expected);
    }

    private static Stream<Arguments> provideSpecialDelimiterCases() {
        return Stream.of(
                Arguments.of("1*2*3", "\\*", List.of(1, 2, 3)),
                Arguments.of("1#2#3", "#", List.of(1, 2, 3))
        );
    }
}
