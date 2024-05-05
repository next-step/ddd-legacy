package stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumbersTest {
    @DisplayName("sum을 실행하면 합계가 산출된다.")
    @Test
    void sum() {
        // given
        var userInput = "1,2,3";

        // when
        int actual = new Numbers(userInput).sum();

        // then
        Assertions.assertThat(actual).isEqualTo(6);
    }
}
