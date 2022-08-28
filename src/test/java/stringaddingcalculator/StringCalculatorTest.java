package stringaddingcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
class StringCalculatorTest {
    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        this.stringCalculator = new StringCalculator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "", " ", "   ", "     ", "       "
    })
    void testAddIfSourceIsNullOrEmptyOrBlankString(final String source) {
        // given when
        final int sum = stringCalculator.add(source);

        // then
        assertThat(sum).isZero();
    }

    @Test
    @DisplayName("단순 숫자 문자열일 경우 숫자 문자열이 int 로 변환된 값을 반환해야한다.")
    void testAddIfSourceIsSimpleNumberFormat() {
        // given
        final String source = "1";

        // when
        final int sum = stringCalculator.add(source);

        // then
        assertThat(sum).isEqualTo(Integer.parseInt(source));
    }

    @Test
    @DisplayName("comma 구분자를 이용하여 덧셈에 성공해야한다.")
    void testAddUsingCommaSeparator() {
        // given
        final String source = "1,2,3,4,5";

        // when
        final int sum = stringCalculator.add(source);

        // then
        assertThat(sum).isEqualTo(15);
    }

    @Test
    @DisplayName("colon 구분자를 이용하여 덧셈에 성공해야한다.")
    void testAddUsingColonSeparator() {
        // given
        final String source = "1:2:3:4:5";

        // when
        final int sum = stringCalculator.add(source);

        // then
        assertThat(sum).isEqualTo(15);
    }

    @Test
    @DisplayName("comma(,) 와 colon(:) 이 복합적으로 존재할지라도 덧셈에 성공해야한다.")
    void testAddUsingCommaAndColonSeparator() {
        // given
        final String source = "1:2,3,4:5";

        // when
        final int sum = stringCalculator.add(source);

        // then
        assertThat(sum).isEqualTo(15);
    }

    @ParameterizedTest
    @CsvSource({
            "//;\\n1;2;3;4;5, 15",
            "//@\\n1@2@3@4@5, 15",
            "//!\\n1!2!3!4!5, 15",
            "//#\\n1#2#3#4#5, 15",
            "//$\\n1$2$3$4$5, 15",
    })
    @DisplayName("사용자가 지정한 separator 로 덧셈이 가능해야한다.")
    void testAddUsingCustomSeparator(final String source, final int expected) {
        // given when
        final int sum = stringCalculator.add(source);

        // then
        assertThat(sum).isEqualTo(expected);
    }

    @Test
    @DisplayName("음수가 전달될 경우 RuntimeException 이 발생해야한다.")
    void testAddIfSourceIsNegative() {
        // given
        final String negativeSource = "1:-2:3";

        // when then
        assertThatThrownBy(() -> stringCalculator.add(negativeSource))
                .isExactlyInstanceOf(RuntimeException.class);
    }
}