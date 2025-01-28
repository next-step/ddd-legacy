package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StringAddCalculatorTest {

    @ParameterizedTest
    @NullAndEmptySource
    void parseAndSum_null_또는_빈문자(String value) {
        int result = StringAddCalculator.parseAndSum(value);
        assertThat(result).isZero();
    }

    @Test
    void parseAndSum_숫자하나() {
        int result = StringAddCalculator.parseAndSum("1");
        assertThat(result).isEqualTo(1);
    }

    @Test
    void parseAndSum_쉼표구분자() {
        int result = StringAddCalculator.parseAndSum("1,2");
        assertThat(result).isEqualTo(3);
    }

    @Test
    void parseAndSum_쉼표_또는_콜론_구분자() {
        int result = StringAddCalculator.parseAndSum("1,2:3");
        assertThat(result).isEqualTo(6);
    }

    @Test
    void parseAndSum_custom_구분자() {
        int result = StringAddCalculator.parseAndSum("//;\n1;2;3");
        assertThat(result).isEqualTo(6);
    }

    @Test
    void parseAndSum_negative() {
        assertThatThrownBy(() -> StringAddCalculator.parseAndSum("-1,2,3"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parseAndSum_문자만() {
        assertThatThrownBy(() -> StringAddCalculator.parseAndSum("abcdefg"))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void parseAndSum_십의자리숫자() {
        int result = StringAddCalculator.parseAndSum("//;\n20;2;30");
        assertThat(result).isEqualTo(52);
    }
}
