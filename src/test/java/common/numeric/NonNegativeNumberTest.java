package common.numeric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NonNegativeNumberTest {

    @DisplayName(value = "0 이상의 정수로 NonNegativeInteger 를 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"0,0", "1,1"})
    void createSuccessTest(final String given, final int expected) {
        assertThat(new NonNegativeNumber(given).getInt())
            .isEqualTo(expected);
    }

    @DisplayName(value = "0 이상의 정수가 아닌 경우, RuntimeException 이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"-1", "a", "*"})
    void createFailTest(final String given) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new NonNegativeNumber(given));
    }
}