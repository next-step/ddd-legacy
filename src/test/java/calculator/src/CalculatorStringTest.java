package calculator.src;

import static org.assertj.core.api.Assertions.assertThat;

import calculator.src.delimiter.Delimiter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.hibernate.annotations.Parameter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorStringTest {

	@DisplayName("토큰을 기반으로 문자를 분리한다.")
	@ParameterizedTest
	@MethodSource("parametersForTokenizingBy")
	void tokenizingBy(String delimiter, String input, List<String> expected) {
		CalculatorString calculatorString = new CalculatorString(input);

		List<CalculatorString> calculatorStrings = calculatorString.tokenizingBy(new Delimiter(delimiter) {
		});

		assertThat(calculatorStrings)
			.map(CalculatorString::getValue)
			.isEqualTo(expected);
	}

	private static Stream<Arguments> parametersForTokenizingBy() {
		return Stream.of(
			Arguments.of("test", "//test\nThis istest token test", List.of("This is", "token")),
			Arguments.of("\\?", "//?\n1?2?    3", List.of("1", "2", "3")),
			Arguments.of(",", "1, 2, 3, 4", List.of("1", "2", "3", "4")),
			Arguments.of(":", "1:2:        3: 4", List.of("1", "2", "3", "4"))
		);
	}

	@DisplayName("커스텀 구분자가 있다면 추출한다.")
	@ParameterizedTest
	@MethodSource("parametersForExtractCustomDelimiter")
	void extractCustomDelimiter(String input, String expected) {
		CalculatorString calculatorString = new CalculatorString(input);
		Optional<Delimiter> delimiter = calculatorString.extractCustomDelimiter();

		SoftAssertions softAssertions = new SoftAssertions();

		softAssertions.assertThat(delimiter).isNotEmpty();
		softAssertions.assertThat(delimiter)
			.map(Delimiter::getValue)
			.get()
			.isEqualTo(expected);

		softAssertions.assertAll();
	}

	private static Stream<Arguments> parametersForExtractCustomDelimiter() {
		return Stream.of(
			Arguments.of("//q\n", "q"),
			Arguments.of("//test\n", "test"),
			Arguments.of("//ab\n", "ab")
		);
	}

	@DisplayName("커스텀 구분자가 없다면 empty를 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"asdf,we,b", "asdea", "iw3sdf"})
	void extractCustomDelimiter_not_exist(String input) {
		CalculatorString calculatorString = new CalculatorString(input);
		Optional<Delimiter> delimiter = calculatorString.extractCustomDelimiter();

		assertThat(delimiter).isEmpty();
	}
}