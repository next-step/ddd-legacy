package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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

	@DisplayName("쉼표 구분자로 구분되는 숫자들의 합을 반환한다.")
	@Test
	void commaDelimiter() {
		assertThat(stringCalculator.calculate("1,2,3")).isEqualTo(6);
	}

	@DisplayName("구분자로 쉼표와 콜론을 섞어서 사용할 수 있다.")
	@Test
	void colonAlsoCanBeDelimiter() {
		assertThat(stringCalculator.calculate("1,2:5")).isEqualTo(8);
	}

	@DisplayName("음수를 전달할 경우 예외가 발생한다.")
	@Test
	void negativeNumberThrowException() {
		assertThatCode(() -> stringCalculator.calculate("1,-2,3"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Negative number not allowed: -2");
	}

	@DisplayName("숫자가 아닌 값을 전달할 경우 예외가 발생한다.")
	@Test
	void onlyNumberCanBeInput() {
		assertThatCode(() -> stringCalculator.calculate("1,*,3"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Character not allowed: *");
	}

	@DisplayName("//와 \\n 사이에 커스텀 구분자를 지정할 수 있다.")
	@Test
	void customDelimiter() {
		assertThat(stringCalculator.calculate("//;\n4;5;6,7:8")).isEqualTo(30);
	}
}
