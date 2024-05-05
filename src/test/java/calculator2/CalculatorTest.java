package calculator2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    @Test
    @DisplayName("수의 스트림을 합산하는 테스트")
    void sumsStreamOfNumbers() {
        //Arrange
        Calculator calculator = new Calculator();
        Stream<Number> numbers = Stream.of(Number.of("2"), Number.of("5"), Number.of("7"));

        //Act
        int result = calculator.sum(numbers);

        //Assert
        assertThat(result).isEqualTo(14);
    }
}