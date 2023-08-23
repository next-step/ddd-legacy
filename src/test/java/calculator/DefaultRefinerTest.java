package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultRefinerTest {

    private DefaultRefiner refiner;

    @BeforeEach
    void setUp() {
        refiner = new DefaultRefiner();
    }

    @DisplayName("유효하지 않은 파라미터를 전달한 경우 예외를 발생시킨다")
    @ParameterizedTest
    @NullSource
    void execute_parameter(final String value) {
        // given

        // when & then
        assertThatThrownBy(() -> refiner.execute(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("soucre가 //;\n로 시작하면서 문자와 ; 이루어져 있으면 분할되어 Numbers를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = "//;\n1;3")
    void execute_custom(final String given) {
        // when
        final PositiveNumbers actual = refiner.execute(given);

        // then
        final PositiveNumbers expected = create(new PositiveNumber(1), new PositiveNumber(3));
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("source가 //;\n로 시작하지 않으면 기본 구분자로 분할되어 Numbers를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = "1:8,6")
    void execute_default(final String given) {
        // when
        final PositiveNumbers actual = refiner.execute(given);

        // then
        final PositiveNumbers expected = create(new PositiveNumber(1), new PositiveNumber(8),
            new PositiveNumber(6));

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private PositiveNumbers create(final PositiveNumber... numbers) {
        return new PositiveNumbers(Arrays.asList(numbers));
    }
}
