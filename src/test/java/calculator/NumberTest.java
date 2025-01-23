package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NumberTest {

    @Test
    @DisplayName("숫자가 아닌 값을 넣으면 예외가 발생한다.")
    void exception_case() {
        // when // then
        assertThatThrownBy(() -> new Number("숫자"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("숫자 외의 값을 넣을 수 없습니다.");
    }

    @ParameterizedTest
    @CsvSource({
            "0, false",
            "1, false",
            "-1, true"
    })
    @DisplayName("숫자가 음수이면 true, 양수이면 false를 반환한다.")
    void is_negative(int number, boolean expected) {
        // given
        Number given = new Number(number);

        // when
        boolean result = given.isNegative();

        // then
        assertThat(result).isEqualTo(expected);
    }
}
