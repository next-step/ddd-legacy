package kitchenpos.stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {
    @DisplayName("0 또는 양수는 정상적으로 처리한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "10", "100"})
    void createPositiveNumber(String numberString) {
        PositiveNumber number = new PositiveNumber(numberString);
        Assertions.assertThat(number.getValue()).isEqualTo(Integer.parseInt(numberString));
    }

    @DisplayName("음수 입력 시 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-10", "-100"})
    void throwExceptionForNegativeNumber(String input) {
        Assertions.assertThatThrownBy(() -> new PositiveNumber(input))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("음수는 포함될 수 없습니다: " + input);
    }


    @DisplayName("숫자 이외의 값 입력 시 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "x12", "ㅁㄴㅇ"})
    void throwExceptionForNonNumericInput(String input) {
        Assertions.assertThatThrownBy(() -> new PositiveNumber(input))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("숫자 이외의 값을 입력할 수 없습니다: " + input);
    }
}
