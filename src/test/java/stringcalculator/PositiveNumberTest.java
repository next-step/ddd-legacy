package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class PositiveNumberTest {

    @Test
    @DisplayName("양수 값으로 PositiveNumber 생성")
    void createPositiveNumberWithPositiveValue() {
        PositiveNumber positiveValue = new PositiveNumber(42);
        assertThat(positiveValue.getNumber()).isEqualTo(42);
    }

    @Test
    @DisplayName("0으로 PositiveNumber 생성")
    void createPositiveNumberWithZero() {
        PositiveNumber positiveValue = new PositiveNumber(0);
        assertThat(positiveValue.getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("음수 값으로 PositiveNumber 생성 시 예외 발생")
    void createPositiveNumberWithNegativeValueThrowsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PositiveNumber(-5));
    }

    @Test
    @DisplayName("유효한 토큰을 파싱하여 PositiveNumber 생성")
    void parseValidToken() {
        PositiveNumber positiveValue = PositiveNumber.parseToken("123");
        assertThat(positiveValue.getNumber()).isEqualTo(123);
    }

    @Test
    @DisplayName("유효하지 않은 토큰 파싱 시 예외 발생")
    void parseInvalidTokenThrowsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> PositiveNumber.parseToken("abc"));
    }

    @Test
    @DisplayName("동일한 값의 PositiveNumber 비교 및 해시 코드 비교")
    void equalsAndHashCode() {
        PositiveNumber value1 = new PositiveNumber(42);
        PositiveNumber value2 = new PositiveNumber(42);
        PositiveNumber value3 = new PositiveNumber(100);

        assertThat(value1).isEqualTo(value2);
        assertThat(value1).isNotEqualTo(value3);
        assertThat(value1.hashCode()).isEqualTo(value2.hashCode());
        assertThat(value1.hashCode()).isNotEqualTo(value3.hashCode());
    }
}
