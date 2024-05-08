package stringcalculator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

	@ParameterizedTest
	@DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
	@ValueSource(strings = {" ", "  "})
	@NullAndEmptySource
	void constructorWithNullOrBlank(final String input) {
		// given
		StringCalculator calculator = new StringCalculator(input);

		// when
		PositiveInteger result = calculator.calculate();

		// then
		assertThat(result).isEqualTo(PositiveInteger.ZERO);
	}

	@ParameterizedTest
	@DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다")
	@ValueSource(strings = {"1", "29", "346", "4987", "56789"})
	void constructorWithOneInteger(final String input) {
		// given
		StringCalculator calculator = new StringCalculator(input);

		// when
		PositiveInteger result = calculator.calculate();

		// then
		assertThat(result).isEqualTo(PositiveInteger.valueOf(input));
	}

	@Test
	@DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다. (예 : “1,2”)")
	void constructorWithTwoIntegers() {
		// given
		StringCalculator calculator = new StringCalculator("1,2");

		// when
		PositiveInteger result = calculator.calculate();

		// then
		assertThat(result).isEqualTo(PositiveInteger.valueOf("3"));
	}

	@Test
	@DisplayName("구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다. (예 : “1,2:3” => 6)")
	void constructorWithColonDelimiter() {
		// given
		StringCalculator calculator = new StringCalculator("1,2:3");

		// when
		PositiveInteger result = calculator.calculate();

		// then
		assertThat(result).isEqualTo(PositiveInteger.valueOf("6"));
	}

	@Test
	@DisplayName("“//”와 “\\n” 문자 사이에 커스텀 구분자를 지정할 수 있다. (예 : “//;\\n1;2;3” => 6)")
	void constructorWithCustomDelimiter() {
		// given
		StringCalculator calculator = new StringCalculator("//;\n1;2;3");

		// when
		PositiveInteger result = calculator.calculate();

		// then
		assertThat(result).isEqualTo(PositiveInteger.valueOf("6"));
	}

	@ParameterizedTest
	@DisplayName("음수나 숫자가 아닌 값을 전달할 경우 RuntimeException 예외가 발생해야 한다. (예 : “-1,2,3”)")
	@ValueSource(strings = {"-1,2,3", "2,a,7", "3,-,14", "김,1,29", "!,72,a"})
	void constructorWithNegativeInteger(final String input) {
		// then
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
			{
				// given
				StringCalculator calculator = new StringCalculator(input);

				// when
				PositiveInteger result = calculator.calculate();
			}
		);
	}
}
