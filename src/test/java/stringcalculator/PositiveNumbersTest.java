package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveNumbersTest {

    @DisplayName(value = "문자열계산기 합계 구하기")
    @Test
    void 성공_합계구하기() {
        assertThat(PositiveNumbers.of("1", "2", "3").sum()).isEqualTo(new PositiveNumber(6));
    }

    @DisplayName(value = "음수가 있을경우 예외 처리")
    @Test
    void 싪패_음수일_경우() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> PositiveNumbers.of("1", "-2", "-3"));
    }
}
