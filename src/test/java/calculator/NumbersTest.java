package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class NumbersTest {

    @DisplayName("문자 리스트를 받아 객체를 생성한다")
    @Test
    void create() {
        assertThatCode(() -> Numbers.from("1", "2", "3"))
                .doesNotThrowAnyException();
    }

    @DisplayName("숫자의 합을 계산한다")
    @Test
    void sum() {
        Numbers numbers = Numbers.from("1", "2", "3");

        int actual = numbers.sum();

        assertThat(actual).isEqualTo(6);
    }
}
