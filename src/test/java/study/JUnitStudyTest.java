package study;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JUnitStudyTest {

    @Test
    void isEqualTo() {
        final BigDecimal expected = new BigDecimal("1.0");
        final BigDecimal actual = new BigDecimal("1.00");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void isEqualByComparingTo() {
        final BigDecimal expected = new BigDecimal("1.0");
        final BigDecimal actual = new BigDecimal("1.00");

        // assertThat(actual).isEqual(expected); // Fail
        assertThat(actual).isEqualByComparingTo(expected);
    }

    @Test
    void isEqualByComparingToByFloat() {
        final BigDecimal expected = new BigDecimal(1.0);
        final BigDecimal actual = new BigDecimal("1.0");

        // assertThat(actual).isEqual(expected); // Fail
        assertThat(actual).isEqualByComparingTo(expected);
    }
}
