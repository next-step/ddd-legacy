package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("문자열 계산기 단위 테스트")
class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @NullAndEmptySource
    void emptyOrNull(final String text) {

        assertThat(calculator.add(text)).isZero();
    }

    /**
     * isSameAs: 캐싱된 객체가 아니면 false 를 반환
     * Integer.valueOf 및 parseInt 모두 동일한 현상 발생
     *
     * @param text
     */
    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @ValueSource(strings = {"0", "1", "2", "3", "4", "5", "10", "100", "127", "128", Integer.MAX_VALUE + ""})
    void oneNumber(final String text) {
        assertThat(calculator.add(text)).isEqualTo(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자가 아닌 문자 하나를 문자열로 입력할 경우 예외를 발생한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @ValueSource(strings = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"})
    void oneString(final String text) {
        assertThatThrownBy(() -> calculator.add(text))
                .isInstanceOf(NumberFormatException.class);
    }
}
