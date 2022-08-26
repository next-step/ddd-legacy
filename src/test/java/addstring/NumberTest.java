package addstring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class NumberTest {

    @Test
    @DisplayName("문자열 숫자 배열에 음수가 있을 수 없다.")
    void convertStringNumbersToIntSumFailTest() {
        String stringNumber = "-1";
        assertThatThrownBy(
            () -> new Number(stringNumber)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("문자열 숫자 배열에 숫자 이외의 것들이 있을 수 없다.")
    void invalidNumberFailTest() {
        String stringNumber = "a";
        assertThatThrownBy(
            () -> new Number(stringNumber)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
