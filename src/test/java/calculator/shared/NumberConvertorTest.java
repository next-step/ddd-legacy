package calculator.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumberConvertorTest {

    private NumberConvertor sut;

    @BeforeEach
    void setUp() {
        sut = new NumberConvertor();
    }

    @DisplayName("문자열 리스트 제공시 양수 일급 컬렉션으로 변환해야 한다.")
    @Test
    void shouldStringListConvertToPositiveNumbers() {
        // Given
        List<String> values = List.of("1", "2", "3");

        // When
        PositiveNumbers positiveNumbers = sut.convertToPositiveNumbers(values);

        // Then
        assertNotNull(positiveNumbers);
        assertEquals(3, positiveNumbers.size());
        assertEquals(6, positiveNumbers.sum());
    }

    @DisplayName("빈 문자열 리스트 제공시 빈 양수 일급 컬렉션으로 변환해야 한다.")
    @Test
    void shouldEmptyStringListConvertToEmptyPositiveNumbers() {
        // Given
        List<String> values = List.of();

        // When
        PositiveNumbers positiveNumbers = sut.convertToPositiveNumbers(values);

        // Then
        assertNotNull(positiveNumbers);
        assertTrue(positiveNumbers.isEmpty());
        assertEquals(0, positiveNumbers.sum());
    }

    @DisplayName("숫자가 아닌 값이 포함되면 NumberFormatException 예외가 발생해야 한다.")
    @Test
    void shouldThrowNumberFormatExceptionWhenIncludingNonNumberValue() {
        // Given
        List<String> values = List.of("1", "a", "3");

        // When & Then
        assertThrows(NumberFormatException.class, () -> sut.convertToPositiveNumbers(values));
    }

    @DisplayName("음수가 포함되면 IllegalArgumentException 예외가 발생해야 한다.")
    @Test
    void 음수가_포함되면_예외_발생() {
        // Given
        List<String> values = List.of("1", "-2", "3");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> sut.convertToPositiveNumbers(values));
    }
}