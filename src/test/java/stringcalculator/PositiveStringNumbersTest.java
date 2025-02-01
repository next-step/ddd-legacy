package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.junit.jupiter.api.Assertions.*;

class PositiveStringNumbersTest {

    @DisplayName(value = "숫자로 된 문자열 배열의 입력은 합을 반환한다.")
    @Test
    void stringNumbers() {
        // given
        String[] inputs = new String[]{"1", "2"};

        // when
        PositiveStringNumbers sut = new PositiveStringNumbers(inputs);

        // then
        assertThat(sut.addAllNumber()).isEqualTo(3);
    }

    @DisplayName(value = "숫자가 아닌 문자열이 포함된 배열은 RuntimeException을 발생시킨다.")
    @Test
    void notStringNumbers() {
        // given
        String[] inputs = new String[]{"1", "a"};

        // when
        // then
        assertThatRuntimeException()
            .isThrownBy(() -> new PositiveStringNumbers(inputs));
    }

    @DisplayName(value = "음수가 포함된 배열은 RuntimeException을 발생시킨다.")
    @Test
    void negativeNumbers() {
        // given
        String[] inputs = new String[]{"-1", "a"};

        // when
        // then
        assertThatRuntimeException()
            .isThrownBy(() -> new PositiveStringNumbers(inputs));
    }
}