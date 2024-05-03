package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {

    @DisplayName("입력받은 숫자들의 합을 반환한다.")
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
