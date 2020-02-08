package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import calculator.StringCalculator.Splitter;

class SplitterTest {

    @NullAndEmptySource
    @ValueSource(strings = { "    " })
    @ParameterizedTest
    void construct_when_regex_is_blank(String regex) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Splitter(regex));
    }

    @Test
    void split() {
        assertThat(StringCalculator.DEFAULT_SPLITTER.split("1,2:3"))
            .isEqualTo(PositiveNumbers.from(1, 2, 3));
    }

    @NullAndEmptySource
    @ValueSource(strings = "    ")
    @ParameterizedTest
    void split_when_text_is_blank(String text) {
        assertThat(StringCalculator.DEFAULT_SPLITTER.split(text))
            .isEqualTo(PositiveNumbers.from());
    }
}