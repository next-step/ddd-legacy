package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("문자열 덧셈 계산기 테스트")
class StringAddCalculatorTest {

	@DisplayName("기본 구분자 이용 문자열 덧셈 계산 테스트")
	@Test
	void splitAndSumTest() {
		//given
		String target = "1,2:3";

		//when
		int result = StringAddCalculator.calculate(target);

		//then
		assertThat(result)
			.isEqualTo(6);
	}

	@DisplayName("커스텀 구분자 이용 문자열 덧셈 계산 테스트")
	@Test
	void splitAndSumWithCustomDelimiterTest() {
		//given
		String target = "//;\n1;2;3";

		//when
		int result = StringAddCalculator.calculate(target);

		//then
		assertThat(result)
			.isEqualTo(6);
	}

	@DisplayName("음수 포함된 경우 RuntimeException 발생")
	@Test
	void throwRuntimeExceptionWhenNegative() {
		//given, when
		String target = "-1,2,3";

		//then
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> StringAddCalculator.calculate(target))
			.withMessageContaining("must not be negative");
	}

	@DisplayName("숫자가 아닌 문자 포함된 경우 RuntimeException 발생")
	@Test
	void throwRuntimeExceptionWhenNotNumeric() {
		//given, when
		String target = "1,2,a";

		//then
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> StringAddCalculator.calculate(target));
	}

	@DisplayName("null 또는 빈 문자열일 경우 0 반환")
	@ParameterizedTest
	@NullAndEmptySource
	void returnZeroWhenNullOrEmptyTest(String target) {
		//when
		int result = StringAddCalculator.calculate(target);

		//then
		assertThat(result)
			.isZero();
	}

}
