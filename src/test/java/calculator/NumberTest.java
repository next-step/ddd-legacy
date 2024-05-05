package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("숫자 테스트")
public class NumberTest {

    @DisplayName("숫자 생성")
    @Test
    void numberCreation() {
        Number number = Number.of("1");
        assertThat(number.getNumber()).isEqualTo(1);
    }

    @DisplayName("문자열을 숫자로 변환 실패")
    @Test
    void failedStringToNumberConversion() {
        assertThatCode(() -> Number.of("invalid"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid number");
    }

    @DisplayName("음수 예외 처리")
    @Test
    void negativeNumberException() {
        assertThatCode(() -> Number.of("-1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Number must be positive");
    }
}