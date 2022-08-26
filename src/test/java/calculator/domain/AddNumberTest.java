package calculator.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AddNumberTest {

    @DisplayName("더해질 숫자는 음수일 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4, -5})
    void negativeException(int negativeNumber) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new AddNumber(negativeNumber));
    }

    @DisplayName("정수 외 값은 더해질 숫자가 될 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "#", "AB", "C"})
    void negativeException(String invalidString) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> AddNumber.from(invalidString));
    }
}
