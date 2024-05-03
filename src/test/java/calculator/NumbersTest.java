package calculator;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class NumbersTest {

    @DisplayName("생성")
    @Test
    void createNumbersTest() {

        Assertions.assertThatCode(() -> new Numbers(new String[]{"1", "2", "3"}))
                .doesNotThrowAnyException();
    }

    @DisplayName("합계")
    @Test
    void sumTest() {

        int sum = new Numbers(new String[]{"1", "2", "3"}).sum();
        assertThat(sum).isEqualTo(6);
    }

}