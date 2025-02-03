package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumbersTest {
    @Test
    @DisplayName("숫자 리스트를 생성하면 모든 요소가 PositiveNumber 객체로 변환된다")
    void shouldConvertStringArrayToPositiveNumberList() {
        String[] input = {"1", "2", "3"};
        Numbers numbers = new Numbers(input);

        List<PositiveNumber> result = numbers.getNumbers();
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getValue());
        assertEquals(2, result.get(1).getValue());
        assertEquals(3, result.get(2).getValue());
    }

    @Test
    @DisplayName("숫자 리스트의 합을 계산할 수 있다")
    void shouldCalculateSumOfNumbers() {
        String[] input = {"1", "2", "3"};
        Numbers numbers = new Numbers(input);

        assertEquals(6, numbers.sum());
    }

    @Test
    @DisplayName("음수가 포함된 입력이 들어오면 예외가 발생해야 한다")
    void shouldThrowExceptionForNegativeNumbers() {
        String[] input = {"1", "-2", "3"};

        assertThrows(RuntimeException.class, () -> new Numbers(input));
    }

}