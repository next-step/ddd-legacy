package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CustomStringSplitterTest {

    private CustomStringSplitter customStringSplitter;

    @BeforeEach
    void setUp() {
        customStringSplitter = new CustomStringSplitter();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null 전달 시 빈 문자열 배열을 반환한다.")
    void emptySplit(final String input) {
        assertThat(customStringSplitter.split(input)).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("//'와 '\n' 사이에 커스텀 구분자를 지정하고, 해당 구분자로 구분된 문자열 전달 시 해당 구분자로 분할된 문자열 배열을 반환한다.")
    @MethodSource("customDelimiterArguments")
    void customDelimiterSplit(final String input, final String[] expected) {
        assertThat(customStringSplitter.split(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("커스텀 문자열 구분자 포맷이 아닐 경우 IllegalArgumentException 처리한다")
    @ValueSource(strings = {
        "invalid",
        "1,2",
        "test"
    })
    void unsupportedFormatSplit(final String input) {
        assertThatThrownBy(() -> customStringSplitter.split(input))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("커스텀 문자열 구분자 포맷이 아닐 경우 false 반환한다.")
    @ValueSource(strings = {
        "invalid",
        "1,2",
        "test"
    })
    void unsupportedFormatSupport(final String input) {
        assertThat(customStringSplitter.support(input)).isFalse();
    }

    @ParameterizedTest
    @DisplayName("커스텀 문자열 구분자 포맷이 맞을 경우 true 반환한다.")
    @ValueSource(strings = {
        "//;\n1;2",
        "//.\n5.6"
    })
    void supportedFormatSupport(final String input) {
        assertThat(customStringSplitter.support(input)).isTrue();
    }

    private static List<Arguments> customDelimiterArguments() {
        return List.of(
            Arguments.of("//;\n1;2", new String[]{"1", "2"}),
            Arguments.of("//.\n5.6", new String[]{"5", "6"})
        );
    }
}