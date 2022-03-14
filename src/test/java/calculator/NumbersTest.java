package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class NumbersTest {

    @DisplayName("values가 null 이면 NullPointException을 발생시킨다")
    @Test
    void parse_when_null_values() {
        // given

        // when & then
        assertThatThrownBy(() -> Numbers.parse(null))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("values가 없다면 덧셈의 결과는 0이다")
    @Test
    void add_when_empty_values() {
        // given
        final Numbers numbers = Numbers.parse(new ArrayList<>());

        // when
        final int result = numbers.add();

        // then
        assertThat(result).isZero();
    }

    @DisplayName("values를 모두 더한 값을 반환한다")
    @ParameterizedTest
    @MethodSource(value = "calculator.NumberProvider#twoZeroOrPositiveNumberProvider")
    void add(int[] rawValues) {
        // given
        final List<String> values
            = Arrays.asList(String.valueOf(rawValues[0]), String.valueOf(rawValues[1]));

        final Numbers numbers = Numbers.parse(values);

        // when
        final int result = numbers.add();

        // then
        final int expResult = rawValues[0] + rawValues[1];
        assertThat(result).isEqualTo(expResult);
    }
}
