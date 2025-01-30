package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringCalculatorTest {

	private final StringCalculator STRING_CALCULATOR = new StringCalculator();

	@DisplayName("구분자가 포함된 문자열로 총 합을 구할 수 있다.")
	@CsvSource({
		"'1,1:2', 4",
		"'10,1,2', 13",
		"'1:1:2', 4",
		"100, 100",
		"'//;\n1;2;3', 6",
		"'//!\n1!2!7', 10",
		"'//@\n10@2@3', 15"
	})
	@ParameterizedTest
	void calculateSum(String inputString, int expected) {
		// Given
		Input input = new Input(inputString);

		// when
		int sum = STRING_CALCULATOR.calculateSum(input);

		// then
		assertThat(sum).isEqualTo(expected);
	}

	@DisplayName("숫자가 아니거나, 음수인 문자열을 입력하면 예외를 발생시킨다.")
	@CsvSource({"삼", "-1", "abc", "!2"})
	@ParameterizedTest
	void calculateSumException(String inputString) {
		// when, then
		assertThatThrownBy(
			() -> {
				Input input = new Input(inputString);
				STRING_CALCULATOR.calculateSum(input);
			})
			.isInstanceOf(RuntimeException.class)
			.satisfies(
				e ->
					assertThat(e.getMessage())
						.matches(
							msg ->
								msg.equals(ErrorMessage.NOT_NUMBER)
									|| msg.equals(ErrorMessage.NEGATIVE_NUMBER)));
	}
}
