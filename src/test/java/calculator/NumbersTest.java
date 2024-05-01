package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {

    @DisplayName("문자열의 배열을 입력 받아 합의 결과를 반환한다.")
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
