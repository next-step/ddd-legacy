package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PositiveNumberTest {

    @DisplayName(value = "양수 클래스를 생성한다")
    @Test
    void construct() {
        assertThat(new PositiveNumber(1).getNumber()).isSameAs(1);
    }

    @DisplayName(value = "서로 다른 양수 클래스를 더한다")
    @Test
    void add() {
        PositiveNumber positiveNumber1 = new PositiveNumber(1);
        PositiveNumber positiveNumber2 = new PositiveNumber(1);

        assertThat(positiveNumber1.add(positiveNumber2).getNumber()).isSameAs(2);
    }

    @DisplayName(value = "초기 값을 생성한다")
    @Test
    void zero() {
        assertThat(PositiveNumber.zero().getNumber()).isSameAs(0);
    }

    @DisplayName(value = "문자열을 숫자로 변환시킨다.")
    @Test
    void stringToInt() {
        assertThat(PositiveNumber.of("1").getNumber()).isSameAs(1);
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(PositiveNumber.of(text).getNumber()).isZero();
    }


    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber(-2));
    }
}
