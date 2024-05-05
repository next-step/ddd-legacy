package stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class NumberTest {

    @DisplayName("Number를 생성할 수 있다.")
    @Test
    void of() {
        // given
        var number = "8";

        // when
        var actual = Number.of(number);

        // then
        var expected = new Number(8);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("음수나 문자를 넣을 경우 예외가 발생한다.")
    @ParameterizedTest()
    @ValueSource(strings = {"-1", "a"})
    void negativeOrStringExceptionTest(String userInput) {
        // when & then
        Assertions.assertThatThrownBy(()-> {
            Number.of(userInput);
        });
    }
}
