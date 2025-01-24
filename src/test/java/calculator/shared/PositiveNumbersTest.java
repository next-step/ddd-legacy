package calculator.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class PositiveNumbersTest {

    @Test
    @DisplayName("정수 리스트에서 PositiveNumbers를 생성할 수 있다.")
    void shouldCreatePositiveNumbersFromIntegerList() {
        // Given
        List<Integer> values = List.of(1, 2, 3, 4, 5);

        // When
        PositiveNumbers positiveNumbers = PositiveNumbers.from(values);

        // Then
        assertNotNull(positiveNumbers);
        assertEquals(5, positiveNumbers.size()); // 요소 개수 확인
    }

    @Test
    @DisplayName("PositiveNumbers의 합계를 올바르게 계산한다.")
    void shouldCalculateSumCorrectly() {
        // Given
        List<Integer> values = List.of(1, 2, 3, 4, 5);

        // When
        PositiveNumbers positiveNumbers = PositiveNumbers.from(values);

        // Then
        assertEquals(15, positiveNumbers.sum()); // 1 + 2 + 3 + 4 + 5 = 15
    }

    @Test
    @DisplayName("빈 리스트를 생성하면 합계는 0이 된다.")
    void shouldReturnZeroSumForEmptyList() {
        // Given
        List<Integer> values = List.of();

        // When
        PositiveNumbers positiveNumbers = PositiveNumbers.from(values);

        // Then
        assertEquals(0, positiveNumbers.sum());
        assertTrue(positiveNumbers.isEmpty());
    }

    @Test
    @DisplayName("음수가 포함되면 예외가 발생한다.")
    void shouldThrowExceptionWhenNegativeNumberIncluded() {
        // Given
        List<Integer> values = List.of(1, -2, 3);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> PositiveNumbers.from(values));
    }
}