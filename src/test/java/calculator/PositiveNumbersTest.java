package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {

    @DisplayName(value = "null을 넣었을 때 사이즈가 0인지 체크.")
    @Test
    void isEqualToTest() {
        assertThat(new PositiveNumbers(null).size())
                .isZero();
    }

    @DisplayName(value = "컬렉션 사이즈 체크.")
    @Test
    void collectionSizeTest() {
        String[] stringNumbers = new String[]{"1", "2"};
        assertThat(new PositiveNumbers(stringNumbers).size())
                .isEqualTo(stringNumbers.length);
    }

    @DisplayName(value = "plus 계산이 제대로 동작하는지 확인 한다.")
    @Test
    void plusTest() {
        String[] stringNumbers = new String[]{"1", "2"};
        assertThat(new PositiveNumbers(stringNumbers).sum()).isEqualTo(new PositiveNumber(3));
    }
}