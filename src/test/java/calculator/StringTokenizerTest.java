package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StringTokenizerTest {
    private StringTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new StringTokenizer();
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자 토큰을 반환한다.")
    @ParameterizedTest
    @MethodSource("provideTwoNumbers")
    void twoNumbers(final String text, final StringOperands expected) {
        assertThat(tokenizer.tokenize(text)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideTwoNumbers() {
        return Stream.of(
                Arguments.of("1,2", StringOperands.of("1,2", ",|:"))
        );
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용해 토큰화 할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideColons")
    void colons(final String text, final StringOperands expected) {
        assertThat(tokenizer.tokenize(text)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideColons() {
        return Stream.of(
                Arguments.of("1,2:3", StringOperands.of("1,2:3", ",|:"))
        );
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정해 토큰화 할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideCustomDelimiter")
    void customDelimiter(final String text, final StringOperands expected) {
        assertThat(tokenizer.tokenize(text)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideCustomDelimiter() {
        return Stream.of(
                Arguments.of("//;\n1;2;3", StringOperands.of("1;2;3", ",|:|;"))
        );
    }
}
