package calculator.test;

import calculator.Number;
import calculator.TextSeparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NumberTest {
    @DisplayName("text 를 받아 객체 생성")
    @Test
    void constructor() {
        Number number = new Number("3");
        assertThat(number.intValue()).isEqualTo(3);
    }

    @DisplayName("text 가 음수일때 오류 발생")
    @Test
    void constructor_isNotAmniotic() {
        assertThatThrownBy(() -> new TextSeparator("-1"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("text 가 숫자가 아닐때 오류 발생")
    @Test
    void constructor_isNotNumberAnd() {
        assertThatThrownBy(() -> new TextSeparator("a"))
                .isInstanceOf(RuntimeException.class);
    }
}
