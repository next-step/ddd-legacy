package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {

    @DisplayName("Numbers 는 Numbers 의 총합을 제공한다.")
    @Test
    void sumTest() {
        // given
        Numbers numbers = new Numbers("1", "2", "3", "4");

        // when
        Number result = numbers.sum();

        // then
        Assertions.assertThat(result).isEqualTo(Number.from("10"));
    }
}
