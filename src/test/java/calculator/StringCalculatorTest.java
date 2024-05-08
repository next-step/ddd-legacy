package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringCalculatorTest {

	StringCalculator stringCalculator;

	@BeforeEach
	void setUp() {
		stringCalculator = new StringCalculator();
	}

	@DisplayName("빈 문자열 또는 null을 입력하는 경우 0을 반환한다.")
	@Test
	void nullOrEmptyReturnZero() {
		assertThat(stringCalculator.calculate("")).isEqualTo(0);
		assertThat(stringCalculator.calculate(null)).isEqualTo(0);
	}
}
