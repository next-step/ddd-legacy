package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumbersTest {

    @Test
    @DisplayName("문자열 수의 합을 합산하는 테스트")
    void sumsStreamOfNumbers() {
        //Arrange
        String[] strings = {"2", "5", "7"};
        Numbers numbers = new Numbers(strings);

        //Act
        int result = numbers.sum();

        //Assert
        assertThat(result).isEqualTo(14);
    }
}