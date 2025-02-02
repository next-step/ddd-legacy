package mission.step1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveNumberTest {

    @Test
    @DisplayName("양수를 정상적으로 생성한다")
    void createPositiveNumber() {
        // given
        String input = "42";

        // when
        PositiveNumber number = PositiveNumber.from(input);

        // then
        assertThat(number.getValue()).isEqualTo(42);
    }

    @Test
    @DisplayName("0을 정상적으로 생성한다")
    void createZero() {
        // given
        String input = "0";

        // when
        PositiveNumber number = PositiveNumber.from(input);

        // then
        assertThat(number.getValue()).isZero();
    }

    @Test
    @DisplayName("음수를 입력하면 예외가 발생한다")
    void throwExceptionForNegativeNumber() {
        // given
        String input = "-1";

        // when, then
        assertThatThrownBy(() -> PositiveNumber.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음수는 허용되지 않습니다: -1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12.34", "1e5"})
    @DisplayName("숫자 형식이 아닌 입력에 대해 예외가 발생한다")
    void throwExceptionForInvalidFormat(String input) {
        assertThatThrownBy(() -> PositiveNumber.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("숫자 형식이 아닙니다");
    }

    @Test
    @DisplayName("앞뒤 공백이 있는 입력을 정상적으로 처리한다")
    void handleWhitespace() {
        // given
        String input = " 123 ";

        // when
        PositiveNumber number = PositiveNumber.from(input);

        // then
        assertThat(number.getValue()).isEqualTo(123);
    }
}
