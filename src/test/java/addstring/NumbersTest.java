package addstring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class NumbersTest {

    @Test
    @DisplayName("문자열 숫자 배열을 성공적으로 계산한다.")
    void convertStringNumbersToIntSumTest() {
        String[] array = new String[]{"1", "2", "3"};
        assertThat(Numbers.from(array).sum()).isEqualTo(6);
    }

    @Test
    @DisplayName("문자열 숫자 배열에 음수가 있을 수 없다.")
    void convertStringNumbersToIntSumFailTest() {
        String[] array = new String[]{"-1", "2", "3"};
        assertThatThrownBy(
            () -> Numbers.from(array).sum()
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("문자열 숫자 배열에 숫자 이외의 것들이 있을 수 없다.")
    void invalidNumberFailTest() {
        String[] array = new String[]{"a", "2", "3"};
        assertThatThrownBy(
            () -> Numbers.from(array).sum()
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
