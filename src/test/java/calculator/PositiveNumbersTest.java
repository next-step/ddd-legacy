package calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumbersTest {
    private PositiveNumbers positiveNumbers;
    @BeforeEach
    void setUp() {
        List<PositiveNumber> positiveNumberList = new ArrayList<>();
        positiveNumberList.add(new PositiveNumber(1));
        positiveNumberList.add(new PositiveNumber(2));
        positiveNumberList.add(new PositiveNumber(3));
        positiveNumbers = new PositiveNumbers(positiveNumberList);
    }

    @DisplayName("positiveNumbers의 합은 6이다.")
    @Test
    void sum() {
        assertEquals(6, positiveNumbers.sum());
    }
}