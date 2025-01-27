package mission.step1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1:2:3",
            "1,2,3",
            "1:2,3"
    })

    void splitStringExpression(String input) {
        String[] result = stringCalculator.splitWithDelimiter(input);

        // 모든 케이스에서 결과는 ["1", "2", "3"]이어야 함
        assertArrayEquals(
                new String[]{"1", "2", "3"},
                result
        );
    }


}