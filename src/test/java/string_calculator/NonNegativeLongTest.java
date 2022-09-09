package string_calculator;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Random;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NonNegativeLongTest {

    @DisplayName("value는 양의 정수일 수 있다.")
    @MethodSource("randomPositiveInteger")
    @ParameterizedTest
    void construct_with_positive_integer(final long l) {
        assertThatNoException().isThrownBy(() -> new NonNegativeLong(l));
    }

    @DisplayName("value는 0일 수 있다.")
    @Test
    void construct_with_0() {
        assertThatNoException().isThrownBy(() -> new NonNegativeLong(0));
    }

    @DisplayName("value는 음의 정수일 수 없다.")
    @MethodSource("randomNegativeInteger")
    @ParameterizedTest
    void construct_with_negative_integer(final long l) {
        assertThatIllegalArgumentException().isThrownBy(() -> new NonNegativeLong(l));
    }

    @DisplayName("value는 양의 정수인 문자열일 수 있다.")
    @MethodSource("randomPositiveInteger")
    @ParameterizedTest
    void construct_with_positive_integer_string(final long l) {
        final String string = String.valueOf(l);
        assertThatNoException().isThrownBy(() -> new NonNegativeLong(string));
    }

    @DisplayName("value는 \"0\"일 수 있다.")
    @Test
    void construct_with_0_string() {
        assertThatNoException().isThrownBy(() -> new NonNegativeLong("0"));
    }

    @DisplayName("value는 음의 정수인 문자열일 수 없다.")
    @MethodSource("randomNegativeInteger")
    @ParameterizedTest
    void construct_with_negative_integer_string(final long l) {
        final String string = String.valueOf(l);
        assertThatIllegalArgumentException().isThrownBy(() -> new NonNegativeLong(string));
    }

    @DisplayName("value는 null 또는 empty일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void construct_with_null_or_empty_string(final String string) {
        assertThatIllegalArgumentException().isThrownBy(() -> new NonNegativeLong(string));
    }

    @DisplayName("value는 blank일 수 없다.")
    @ValueSource(strings = {"  ", "\t"})
    @ParameterizedTest
    void construct_with_blank_string(final String string) {
        assertThatIllegalArgumentException().isThrownBy(() -> new NonNegativeLong(string));
    }

    private static LongStream randomPositiveInteger() {
        final Random random = new Random();
        return random.longs(100)
                .map(Math::abs);
    }

    private static LongStream randomNegativeInteger() {
        return randomPositiveInteger().map((l) -> l * (-1));
    }
}
