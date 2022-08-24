package calculator.src;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {

	@DisplayName("숫자 포멧이면 정상적으로 객체가 생성된다.")
	@ParameterizedTest
	@ValueSource(strings = {"2", "3", "4"})
	void constructor(String input) {
		assertThatCode(() -> PositiveNumber.valueOf(input))
			.doesNotThrowAnyException();
	}

	@DisplayName("숫자 포멧이 아니면 exception")
	@ParameterizedTest
	@ValueSource(strings = {"2.", "3v", "d"})
	void constructor_exception_when_invalid_format(String input) {
		assertThatThrownBy(() -> PositiveNumber.valueOf(input))
			.isInstanceOf(RuntimeException.class);
	}

	@DisplayName("음수면 exception")
	@ParameterizedTest
	@ValueSource(strings = {"-2", "-3", "-4"})
	void constructor_exception_when_negative_number(String input) {
		assertThatThrownBy(() -> PositiveNumber.valueOf(input))
			.isInstanceOf(RuntimeException.class);
	}

	@DisplayName("두 수를 더할 수 있다.")
	@ParameterizedTest
	@CsvSource(value = {"2, 3, 5", "1, 2, 3", "5, 4, 9"})
	void sum(String a, String b, String result) {
		PositiveNumber aNumber = PositiveNumber.valueOf(a);
		PositiveNumber bNumber = PositiveNumber.valueOf(b);

		PositiveNumber sumNumber = aNumber.sum(bNumber);

		assertThat(sumNumber).isEqualTo(PositiveNumber.valueOf(result));
	}
}