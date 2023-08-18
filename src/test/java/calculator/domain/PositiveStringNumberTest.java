package calculator.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveStringNumberTest {

    @DisplayName("양수 문자열 숫자 객체를 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2", "3", "4", "5"})
    void testInitPositiveStringNumber(String value) {
        // when // then
        assertDoesNotThrow(() -> PositiveStringNumber.of(value));
    }

    @DisplayName("잘못된 값으로 양수 문자열 숫자 객체를 생성하면 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"-2", "-1", "a", "A", "-"})
    void testInitPositiveStringNumberIfNotValidValue(String value) {
        // when // then
        assertThrows(RuntimeException.class, () -> PositiveStringNumber.of(value));
    }
}
