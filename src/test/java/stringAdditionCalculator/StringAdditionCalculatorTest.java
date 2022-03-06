package stringAdditionCalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringAdditionCalculatorTest {
	private StringAdditionCalculator stringAdditionCalculator;

	@BeforeEach
	void setUp() {
		stringAdditionCalculator = new StringAdditionCalculator();
	}

	@DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
	@ParameterizedTest
	@ValueSource(strings = {"//;\n1;2;3"})
	void customSeparator(final String numbersWithCustomSeparator) {
		assertThat(stringAdditionCalculator.sum(numbersWithCustomSeparator)).isEqualTo(6);
	}

	@DisplayName("빈 문자열이 있으면 0을 반환하고 나머지의 합을 반환한다")
	@Test
	void empty_strings_is_one_of_input_then_return_zero_and_sum_of_the_rest() {
		String numbersWithEmptyStrings = ",3";

		assertThat(stringAdditionCalculator.sum(numbersWithEmptyStrings)).isEqualTo(3);
	}

	@DisplayName("숫자가 아닌 문자열이 포함되어 있으면 에러가 발생한다")
	@Test
	void if_not_number_string_is_contained_error_occurs() {
		String numbersWithNotNumberString = "1,2,a";

		assertThatThrownBy(() -> stringAdditionCalculator.sum(numbersWithNotNumberString)).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("문자 숫자 하나를 입력할 경우 해당 숫자를 반환한다")
	@Test
	void single_number_is_input_then_return_the_number() {
		String singleNumberString = "5";

		assertThat(stringAdditionCalculator.sum(singleNumberString)).isEqualTo(5);
	}

	@DisplayName("null을 입력할 경우 0을 반환한다")
	@Test
	void null_is_input_then_return_zero() {
		String nullString = null;

		assertThat(stringAdditionCalculator.sum(nullString)).isZero();
	}

	@DisplayName("빈 문자열을 입력할 경우 0을 반환한다")
	@Test
	void empty_string_is_input_then_return_zero() {
		String emptyString = "";

		assertThat(stringAdditionCalculator.sum(emptyString)).isZero();
	}

	@DisplayName("쉼표 또는 콜론을 섞어 가지는 문자열을 전달할 경우 각 숫자의 합을 반환한다")
	@Test
	void sum_values_that_are_consist_of_numbers_with_comma_or_colon_separator_combined() {
		String numbersWithCommaOrColon = "12,34:56";

		assertThat(stringAdditionCalculator.sum(numbersWithCommaOrColon)).isEqualTo(102);
	}

	@DisplayName("콜론을 구분자로 가지는 문자열을 전달할 경우 각 숫자의 합을 반환한다")
	@Test
	void sum_values_that_are_consist_of_numbers_with_colon_separator() {
		String numbersWithColon = "12:34";
		StringAdditionCalculator stringAdditionCalculator = new StringAdditionCalculator();
		int sumOfNumbersWithColon = stringAdditionCalculator.sum(numbersWithColon);

		assertThat(sumOfNumbersWithColon).isEqualTo(46);
	}

	@DisplayName("쉼표를 구분자로 가지는 문자열을 전달할 경우 각 숫자의 합을 반환한다")
	@Test
	void sum_values_that_are_consist_of_numbers_with_comma_separator() {
		String numbersWithComma = "12,34";

		assertThat(stringAdditionCalculator.sum(numbersWithComma)).isEqualTo(46);
	}
}
