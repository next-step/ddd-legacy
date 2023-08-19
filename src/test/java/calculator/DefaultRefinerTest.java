package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
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
        final Numbers actual = refiner.execute(given);

        // then
        final Numbers expected = create(new Number(1), new Number(3));
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private String createCustomText(final String delimiter, final List<String> characters) {
        return String.format("//%s\n%s", delimiter, String.join(delimiter, characters));
    }

    @DisplayName("source가 //;\n로 시작하지 않으면 기본 구분자로 분할되어 Numbers를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = "1:-1,6")
    void execute_default(final String given) {
        // when
        final Numbers actual = refiner.execute(given);

        // then
        final Numbers expected = create(new Number(1), new Number(-1), new Number(6));

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private Numbers create(final Number... numbers) {
        return new Numbers(Arrays.asList(numbers));
    }
}
