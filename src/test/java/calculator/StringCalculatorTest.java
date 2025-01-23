package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest(name = "입력 값: {0}, 기대 값: {1}")
    @CsvSource(value = {"1,2|3", "2,3|5", "3,4|7", "4,5|9", "5,6|11"}, delimiter = '|')
    void twoNumbers(final String text, final int expected) {
        assertThat(calculator.add(text)).isSameAs(expected);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest(name = "입력 값: {0}, 기대 값: {1}")
    @CsvSource(value = {"1:2|3", "2:3|5", "3:4|7", "4:5|9", "5:6|11"}, delimiter = '|')
    void colons(final String text, final int expected) {
        assertThat(calculator.add(text)).isSameAs(expected);
    }

    @DisplayName(value = "구분자를 쉼표(,) 및 콜론(:) 이외에 다른 문자열로 사용할 수 없다.")
    @ParameterizedTest(name = "입력 값: {0}, 기대 값: {1}")
    @ValueSource(strings = {"1;2", "2;3", "3;4", "4;5", "5;6"})
    void colons(final String text) {
        assertThatThrownBy(() -> calculator.add(text))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest(name = "입력 값: {0}, 기대 값: {1}")
    @CsvSource(value = {"//;\\n1;2;3|6", "//o\\n1o2o3|6", "//o\\n0o0o1|1"}, delimiter = '|')
    void customDelimiter(final String text, final int expected) {
        assertThat(calculator.add(text)).isSameAs(expected);
    }

    @DisplayName(value = "음수를 전달할 경우 RuntimeException 예외가 발생해야 한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @ValueSource(strings = {"-1,0,1", "1,-2,3", "1,2,-3", "-1,-2,-3"})
    void negativeNumber(final String text) {
        assertThatThrownBy(() -> calculator.add(text))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "숫자가 아닌 구분자 하나를 문자열로 입력할 경우 예외를 발생한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @ValueSource(strings = {";", "|", "//;\\n"})
    void oneDelimiter(final String text) {
        assertThatThrownBy(() -> calculator.add(text))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "기본 구분자를 쉼표(,) 및 콜론(:) 이외에 다른 문자열로 대체할 수 있다.")
    @ParameterizedTest(name = "입력 값: {0}, 기본 구분자 값: {1}, 기대 값: {2}")
    @CsvSource(value = {"1;2;3|;|6", "1o2o3|o|6", "0o0o1|o|1"}, delimiter = '|')
    void changeDefaultDelimiter(final String text, final String delimiter, final int expected) {
        final StringCalculator stringCalculator = new StringCalculator(new DefaultDelimiter(delimiter));
        assertThat(stringCalculator.add(text)).isSameAs(expected);
    }
}
