package calculator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PositiveNumberTest {

	@DisplayName("문자열을 숫자 값으로 가져올 수 있다.")
	@CsvSource({"'100', 100", "'0', 0", "'1', 1"})
	@ParameterizedTest
	void getValue(String inputValue, int expectedValue) {
		// when
		PositiveNumber positiveNumber = new PositiveNumber(inputValue);

		// then
		assertThat(positiveNumber.getValue()).isEqualTo(expectedValue);
	}

	@DisplayName("문자열이 숫자가 아니면 예외를 발생시킨다.")
	@CsvSource({"삼", "일", "이"})
	@ParameterizedTest
	void parseStringToNumberThrowString(String inputValue) {
		// when, then
		assertThatThrownBy(() -> new PositiveNumber(inputValue))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(ErrorMessage.NOT_NUMBER);
	}

	@DisplayName("문자열이 음수면 예외를 발생시킨다.")
	@CsvSource({"-1", "-10"})
	@ParameterizedTest
	void parseStringToNumberThrowNegativeNumber(String inputValue) {
		// when, then
		assertThatThrownBy(() -> new PositiveNumber(inputValue))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(ErrorMessage.NEGATIVE_NUMBER);
	}
}
