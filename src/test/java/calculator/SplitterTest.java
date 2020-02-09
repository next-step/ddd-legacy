package calculator;

import static calculator.PositiveNumbersTest.positiveNumbersFrom;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SplitterTest {
    @Test
    void split() {
        assertThat(Splitter.builder()
                           .with(new Delimiter(","))
                           .with(new Delimiter(":"))
                           .with(new Delimiter(";"))
                           .build().split("1,2:3;44;3:2,11"))
            .isEqualTo(positiveNumbersFrom(1, 2, 3, 44, 3, 2, 11));
    }

    @NullAndEmptySource
    @ValueSource(strings = "    ")
    @ParameterizedTest
    void split_when_text_is_blank(String text) {
        assertThat(Splitter.builder()
                           .build().split(text))
            .isEqualTo(positiveNumbersFrom());
    }
}