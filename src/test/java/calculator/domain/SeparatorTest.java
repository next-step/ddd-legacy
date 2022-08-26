package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SeparatorTest {

    @DisplayName("',' 문자열을 통해 구분자를 찾을 수 있다.")
    @Test
    void rest() {
        Separator separator = Separator.findByText(",");

        assertThat(separator).isEqualTo(Separator.REST);
    }

    @DisplayName("':' 문자열을 통해 구분자를 찾을 수 있다.")
    @Test
    void colon() {
        Separator separator = Separator.findByText(":");

        assertThat(separator).isEqualTo(Separator.COLON);
    }

    @DisplayName("주어진 문자열을 통해 구분자를 찾지 못할 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "#", "AB", "C"})
    void separatorException(String invalidString) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->  Separator.findByText(invalidString));
    }
}
