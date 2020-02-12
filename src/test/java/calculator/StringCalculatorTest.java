package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Geonguk Han
 * @since 2020-02-07
 */
class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 더하기 테스트")
    @ParameterizedTest
    @NullAndEmptySource
    void add_empty(final String inputValue) {
        final int result = stringCalculator.add(inputValue);
        assertThat(result).isEqualTo(0);
    }

    @DisplayName("값이 한개인 경우 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void add_one(final String inputValue) {
        final int result = stringCalculator.add(inputValue);
        assertThat(result).isEqualTo(Integer.parseInt(inputValue));
    }

    @DisplayName("구분자가 , ; 가 있는 더하기 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"1,2;3"})
    void add_delimiter(final String inputValue) {
        final int result = stringCalculator.add(inputValue);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("커스텀한 구분자를 사용하는 더하기 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void add_delimiter_with_custom(final String inputValue) {
        final int result = stringCalculator.add(inputValue);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("음수가 포함된 문자열은 RuntimeException 예외를 반환")
    @ParameterizedTest
    @ValueSource(strings = {"-1,2,3"})
    void add_negative(final String inputValue) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> stringCalculator.add(inputValue));
    }
}
