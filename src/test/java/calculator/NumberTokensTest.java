package calculator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumberTokensTest {
    private NumberTokens numberTokens;
    @BeforeEach
    void setUp() {
        String[] tokens = {"1", "2", "3"};
        numberTokens = new NumberTokens(tokens);
    }

    @DisplayName("numberTokens의 합은 6이다.")
    @Test
    void generateSum() {
        assertEquals(6, numberTokens.generateSum());
    }
}