package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PositiveNumberTest {

    @DisplayName("양의 숫자 객체를 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void can_create(int value) {
        assertThat(new PositiveNumber(value)).isNotNull();
    }

    @DisplayName("음수를 입력하면 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4})
    void negative(int value) {
        assertThatCode(() -> new PositiveNumber(value))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("음수는 허용되지 않습니다. 입력된 수: " + value);
    }
}
