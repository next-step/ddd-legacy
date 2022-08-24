package calculator.src.delimiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import calculator.src.CalculatorString;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DelimitersTest {

	@DisplayName("구분자를 기반으로 문자열을 토큰화 시킨다.")
	@ParameterizedTest
	@MethodSource("parametersForTokenize")
	void tokenize(CalculatorString calculatorString, List<String> expected) {
		Delimiters delimiters = new Delimiters(List.of(
			new Delimiter(",") {
			},
			new Delimiter(":") {
			}
		));

		List<CalculatorString> actual = delimiters.tokenize(calculatorString);

		assertThat(actual)
			.map(CalculatorString::getValue)
			.isEqualTo(expected);
	}

	private static Stream<Arguments> parametersForTokenize() {
		return Stream.of(
			Arguments.of(
				new CalculatorString("1, 2: 3"), List.of("1", "2", "3")
			),
			Arguments.of(
				new CalculatorString("1, 2, 3"), List.of("1", "2", "3")
			),
			Arguments.of(
				new CalculatorString("//test\n1test 2: 3"), List.of("1", "2", "3")
			),
			Arguments.of(
				new CalculatorString("//q\n1q 2      , 3"), List.of("1", "2", "3")
			),
			Arguments.of(
				new CalculatorString("1, 2: 3"), List.of("1", "2", "3")
			)
		);
	}
}