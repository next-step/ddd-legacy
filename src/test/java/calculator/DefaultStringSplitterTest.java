package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultStringSplitterTest {

    private DefaultStringSplitter defaultStringSplitter;

    @BeforeEach
    void setUp() {
        defaultStringSplitter = new DefaultStringSplitter();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null 전달 시 빈 문자열 배열을 반환한다.")
    void emptySplit(final String input) {
        assertThat(defaultStringSplitter.split(input)).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열 포맷이 아닐 경우 분할되지 않은 배열을 반환한다.")
    @MethodSource("unsupportedDelimiterArguments")
    void unsupportedDelimiterSplit(final String input, final String[] expected) {
        assertThat(defaultStringSplitter.split(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열 포맷일 경우 해당 구분자로 분할된 배열을 반환한다.")
    @MethodSource("defaultDelimiterArguments")
    void supportedFormatSplit(final String input, final String[] expected) {
        assertThat(defaultStringSplitter.split(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("어떤 문자열이든 지원한다.")
    @ValueSource(strings = {
        "1,2",
        "5:6",
        "112"
    })
    void supportedFormatSupport(final String input) {
        assertThat(defaultStringSplitter.support(input)).isTrue();
    }

    private static List<Arguments> unsupportedDelimiterArguments() {
        return List.of(
            Arguments.of("112", new String[]{"112"}),
            Arguments.of("1.2", new String[]{"1.2"})
        );
    }

    private static List<Arguments> defaultDelimiterArguments() {
        return List.of(
            Arguments.of("1,2", new String[]{"1", "2"}),
            Arguments.of("5:6", new String[]{"5", "6"})
        );
    }
}