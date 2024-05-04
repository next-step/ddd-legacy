package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;


class PositiveNumberTest {

    @ParameterizedTest
    @DisplayName("양수만 가능하다")
    @ValueSource(ints = {-2, -1, -3})
    void positiveNumber(int input) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PositiveNumber(input));
    }

    @ParameterizedTest
    @DisplayName("양수만 가능하다")
    @ValueSource(strings = {"-2", "-1", "-3"})
    void positiveNumber(String input) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PositiveNumber(input));
    }

}
