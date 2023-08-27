package kitchenpos.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class StringCalculatorTest {
    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }


    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void returnZeroWhenEmptyOrNUll(String input) {
        stringCalculator.add(input);
        assertThat(stringCalculator.getResult()).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void returnSingleNumber() {
        stringCalculator.add("1");
        assertThat(stringCalculator.getResult()).isEqualTo(1);
    }

    @DisplayName("구분자를 컴마(,)와 콜론(:)을 사용할 수 있다.")
    @Test
    void returnSplitDelimiter() {
        stringCalculator.add("1,2:3");
        assertThat(stringCalculator.getResult()).isEqualTo(6);
    }

    @DisplayName("\"//\"와 \"\\n\" 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void returnSplitCustomDelimiter() {
        stringCalculator.add("//;\n1;2;3");
        assertThat(stringCalculator.getResult()).isEqualTo(6);
    }

    @DisplayName("계산기를 초기화 할 수 있다.")
    @Test
    void clearCalculator() {
        stringCalculator.add("1000");
        stringCalculator.clearResult();
        assertThat(stringCalculator.getResult()).isZero();
    }

    @DisplayName("자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void throwExceptionWhenExpressionsHaveNegativeNumber() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> stringCalculator.add("-1"));
    }
}
