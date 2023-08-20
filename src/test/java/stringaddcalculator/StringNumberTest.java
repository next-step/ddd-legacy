package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("숫자문자 도메인 테스트")
class StringNumberTest {

	@DisplayName("생성 테스트")
	@Test
	void createTest() {
		assertThatCode(() -> StringNumber.from("1"))
			.doesNotThrowAnyException();
	}

	@DisplayName("null 또는 빈 문자열일 경우 0 반환")
	@ParameterizedTest
	@NullAndEmptySource
	void parseIntNullOrEmptyTest(String value) {
		//given
		StringNumber stringNumber = StringNumber.from(value);

		//when
		int result = stringNumber.parseInt();

		//then
		assertThat(result)
			.isZero();
	}

	@DisplayName("음수일 경우 예외 발생")
	@ParameterizedTest(name = "{displayName}[{index}] {0} is negative")
	@ValueSource(strings = {"-1", "-2", "-3", "-4", "-5"})
	void parseIntNegativeNumberTest(String value) {
		//given
		StringNumber stringNumber = StringNumber.from(value);

		//when
		Throwable thrown = catchThrowable(stringNumber::parseInt);

		//then
		assertThat(thrown)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("must not be negative");
	}

	@DisplayName("숫자가 아닐 경우 예외 발생")
	@ParameterizedTest(name = "{displayName}[{index}] {0} is not number")
	@ValueSource(strings = {"a", "b", "c", "d", "e"})
	void parseIntNotNumberTest(String value) {
		//given
		StringNumber stringNumber = StringNumber.from(value);

		//when
		Throwable thrown = catchThrowable(stringNumber::parseInt);

		//then
		assertThat(thrown)
			.isInstanceOf(NumberFormatException.class);
	}

	@DisplayName("숫자일 경우 숫자 반환")
	@ParameterizedTest(name = "{displayName}[{index}] {0} is number")
	@ValueSource(strings = {"1", "2", "3", "4", "5"})
	void parseIntNumberTest(String value) {
		//given
		StringNumber stringNumber = StringNumber.from(value);

		//when
		int result = stringNumber.parseInt();

		//then
		assertThat(result)
			.isEqualTo(Integer.parseInt(value));
	}
}
