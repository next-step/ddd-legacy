package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NonNegativeIntegerTest {

    @ParameterizedTest
    @ValueSource(strings = {"0", "1"})
    @DisplayName("음이 아닌 정수에 대한 초기화 작업 테스트")
    void testValidInputs(String input) {
        NonNegativeInteger nonNegativeInteger = NonNegativeInteger.of(input);
        assertEquals(nonNegativeInteger.getInteger(), Integer.parseInt(input));
    }


    @ParameterizedTest
    @ValueSource(strings = {"-100", "-1", ""})
    @DisplayName("허용되지 않은 정수에 대한 초기화 작업 테스트")
    void testInValidInputs(String input) {
        assertThrows(RuntimeException.class, () -> NonNegativeInteger.of(input));
    }
}
