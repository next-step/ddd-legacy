package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveNumberTest {

    @DisplayName("PositiveNumber는 음수 일 수 없다.")
    @Test
    void positiveInteger() {
        assertThatThrownBy(() -> new PositiveNumber("-1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("음수를 입력할 수 없습니다.");
    }

    @DisplayName("PositiveNumber는 숫자여야 한다.")
    @Test
    void notNumber() {
        assertThatThrownBy(() -> new PositiveNumber("숫자"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("숫자가 아닌 값을 입력 할 수 없습니다.");
    }

    @DisplayName("빈 문자열은 0으로 대체된다.")
    @Test
    void emptyString() {
        assertThat(new PositiveNumber("")).isEqualTo(new PositiveNumber("0"));
    }
}