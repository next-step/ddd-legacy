package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberTest {

    private Number number;

    @BeforeEach
    void setUp() {
        number = new Number("20");
    }

    @Test
    @DisplayName("숫자를 생성한다")
    void createNumber() {
        assertThat(number).isEqualTo(new Number("20"));
    }

    @Test
    @DisplayName("정수형 숫자를 리턴한다")
    void getIntegerNumber() {
        assertThat(number.getNumber()).isSameAs(20);
    }

    @ParameterizedTest
    @DisplayName("음수나 숫자가 아닌 문자를 생성하는 경우 IllegalArgumentException이 발생한다")
    @ValueSource(strings = {"-20", "A", "!"})
    void createNumberException(String text) {
        assertThatThrownBy(() -> {
            Number number = new Number(text);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
