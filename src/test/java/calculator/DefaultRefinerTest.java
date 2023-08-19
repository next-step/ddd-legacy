package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @DisplayName("soucre가 //;\n로 시작하면서 문자와 ; 이루어져 있으면 execute의 대상이 된다.")
    @Test
    void execute_custom_whitebox() {
        // given
        final String delimiter = ";";
        final String inputText = createCustomText(delimiter, Arrays.asList("1", "b", "A"));

        // when & then
        assertThat(refiner.getCustomPattern()
            .matcher(inputText)
            .matches())
            .isTrue();
    }

    @DisplayName("soucre가 //;\n로 시작하면서 문자와 ; 이루어져 있으면 분할되어 문자 리스트를 반환한다.")
    @Test
    void execute_custom2_whitebox() {
        // given
        final String delimiter = ";";
        final List<String> dummy = Arrays.asList("1", "b", "A");
        final String inputText = createCustomText(delimiter, dummy);

        // when
        final List<String> actual = refiner.execute(inputText);

        // then
        final List<String> expected = ImmutableList.copyOf(dummy);
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private String createCustomText(final String delimiter, final List<String> characters) {
        return String.format("//%s\n%s", delimiter, String.join(delimiter, characters));
    }

    @DisplayName("source가 문자와 ,또는 : 구분자로 이루어져 있으면 분할되어 문자 리스트를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3", "a,2,3", "1:B:3", "1:2,3", "a:b,c"})
    void execute_default_whitebox(final String value) {
        // given

        // when
        final List<String> actual = refiner.execute(value);

        // then
        final List<String> expected = split(value, refiner.getDefaultRegex());

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private List<String> split(final String value, final String delimiter) {
        return Arrays.asList(value.split(delimiter));
    }

    @DisplayName("전략에 맞는 값을 반환한다.")
    @Test
    void execute_blackbox() {
        // given
        final String given1 = "B:1,6,K";
        final String given2 = "//;\nA;C;3";

        // when
        final List<String> actual1 = refiner.execute(given1);
        final List<String> actual2 = refiner.execute(given2);

        // then
        final List<String> expected1 = Arrays.asList("B", "1", "6", "K");
        final List<String> expected2 = Arrays.asList("A", "C", "3");

        assertThat(actual1)
            .usingRecursiveComparison()
            .isEqualTo(expected1);
        assertThat(actual2)
            .usingRecursiveComparison()
            .isEqualTo(expected2);
    }
}
