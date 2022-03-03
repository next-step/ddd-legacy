package calculation.test;

import calculation.calculator.StringCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StringCalculatorTest {

	@ParameterizedTest(name = "문자열 덧셈 {index} [{arguments}]")
	@DisplayName("문자열을 분리하여 덧셈")
	@MethodSource
	void add(String stringNumbers, int expected) {
		int actual = StringCalculator.add(stringNumbers);
		assertThat(actual).isEqualTo(expected);
	}

	private static Stream<Arguments> add() {
		return Stream.of(
				Arguments.of("", 0),
				Arguments.of(null, 0),
				Arguments.of("1", 1),
				Arguments.of("1,2", 3),
				Arguments.of("1,2:3", 6),
				Arguments.of("//;\n1;2;3", 6),
				Arguments.of("//;\n1;;3", 4)
		);
	}

	@DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
	@Test
	void negative() {
		assertThatExceptionOfType(RuntimeException.class)
				.isThrownBy(() -> StringCalculator.add("-1"));
	}

	@DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
	@Test
	void negative2() {
		assertThatExceptionOfType(RuntimeException.class)
				.isThrownBy(() -> StringCalculator.add("1:-1"));
	}
}
