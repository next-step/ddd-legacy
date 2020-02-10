package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class DelimiterTest {
    @NullAndEmptySource
    @ParameterizedTest
    void construct_when_regex_is_empty(String regex) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Delimiter(regex));
    }

    @Test
    void split() {
        assertThat(new Delimiter(",").split("1,2,3"))
            .isEqualTo(new String[] { "1", "2", "3" });
    }
}