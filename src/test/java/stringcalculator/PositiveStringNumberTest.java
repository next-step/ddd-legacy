package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.junit.jupiter.api.Assertions.*;

class PositiveStringNumberTest {

    @DisplayName(value = "숫자로 된 문자열은 정수형의 값을 반환 할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "11"})
    void stringNumber(final String stringNumber) {
        // when
        PositiveStringNumber sut = new PositiveStringNumber(stringNumber);

        // then
        assertThat(sut.getNumber()).isEqualTo(Integer.parseInt(stringNumber));
    }

    @DisplayName(value = "숫자가 아닌 문자열이 들어가면 RuntimeException을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"a"})
    void notNumber(final String stringNumber) {
        // when
        // then
        assertThatRuntimeException()
            .isThrownBy(() -> new PositiveStringNumber(stringNumber));
    }

    @DisplayName(value = "음수가 입력으로 들어가면 RuntimeException을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negativeNumber(final String stringNumber) {
        // when
        // then
        assertThatRuntimeException()
            .isThrownBy(() -> new PositiveStringNumber(stringNumber));
    }
}