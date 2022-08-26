package calculator.number;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

import calculator.StringCalculator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumberTest {


    @DisplayName("양수만 입력이 가능하다")
    @Test
    void allow_positive_number() {
        final PositiveNumber positiveNumber = new PositiveNumber("3");
        Assertions.assertThat(positiveNumber.value()).isEqualTo(3);
    }

    @DisplayName("음수는 입력이 불가능하다")
    @Test
    void deny_negative_number() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new PositiveNumber("-1"));
    }
}
