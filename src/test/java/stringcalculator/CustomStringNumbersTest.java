package stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CustomStringNumbersTest {


    @Test
    @DisplayName("문자열, 특수문자가 섞여있을 때")
    void sum_test1() {
        CustomStringNumbers customStringNumbers = new CustomStringNumbers("Test,Test:,Test,,:;1,:TEST:/.\"");
        Assertions.assertThat(customStringNumbers.sum()).isOne();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공백이거나 null 테스트")
    void sum_test2(final String test) {
        CustomStringNumbers customStringNumbers = new CustomStringNumbers(test);
        Assertions.assertThat(customStringNumbers.sum()).isZero();
    }

}
