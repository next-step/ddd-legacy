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

	@DisplayName("숫자 하나를 문자열로 입력하는 경우 해당 숫자를 반환한다.")
	@Test
	void oneNumberReturnNumber() {
		assertThat(stringCalculator.calculate("1")).isEqualTo(1);
		assertThat(stringCalculator.calculate("11")).isEqualTo(11);
	}
}
