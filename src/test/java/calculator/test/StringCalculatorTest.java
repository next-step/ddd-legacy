package calculator.test;

import calculator.source.Number;
import calculator.source.StringCalculator;
import calculator.source.splitter.FixedSplitter;
import calculator.source.splitter.RegexSplitter;
import calculator.source.splitter.StringSplitters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StringCalculatorTest {
    private static final String REGEX = "//(.)\n(.*)";
    private static final String FIXED_DELIMITER = ",|:";

    private final StringSplitters splitters = new StringSplitters()
            .add(new RegexSplitter(REGEX))
            .add(new FixedSplitter(FIXED_DELIMITER));
    private final StringCalculator calculator = new StringCalculator(splitters);

    @ParameterizedTest
    @NullAndEmptySource
    void 빈_문자열_또는_null을_입력한_경우_0을_반환한다(final String input) {
        assertThat(calculator.plus(input))
                .isEqualTo(new Number(0));
    }


    @ParameterizedTest
    @ValueSource(strings = {"1", "10"})
    void 숫자_하나를_문자열로_입력할_경우_해당_숫자를_반환한다(final String input) {
        assertThat(calculator.plus(input))
                .isEqualTo(new Number(input));
    }

    @ParameterizedTest
    @CsvSource(value = {"1,2:3", "10,2,3:15"}, delimiter = ':')
    void 숫자_두개를_쉼표_구분자로_입력할_경우_합을_반환한다(final String input, final String expected) {
        assertThat(calculator.plus(input))
                .isEqualTo(new Number(expected));
    }

    @ParameterizedTest
    @CsvSource(value = {"1,2:3", "10,20,30:60"}, delimiter = ':')
    void 구분자를_쉼표_이외에_콜론을_사용할_수_있다(final String input, final String expected) {
        assertThat(calculator.plus(input))
                .isEqualTo(new Number(expected));
    }

    @DisplayName("//와_\\n_문자_사이에_커스텀_구분자를_지정할_수_있다.")
    @ParameterizedTest
    @MethodSource("provideRegexForCustomSplitter")
    void customDelimiter(final String input, final int expected) {
        assertThat(calculator.plus(input))
                .isEqualTo(new Number(expected));
    }

    @Test
    void 문자열_계산기에_음수를_전달하는_경우_RuntimeException_예외_처리를_한다() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.plus("-1"));
    }

    private static Stream<Arguments> provideRegexForCustomSplitter() {
        return Stream.of(
                Arguments.of("//;\n1;2;3", 6),
                Arguments.of("//%\n10%20%30", 60)
        );
    }
}
