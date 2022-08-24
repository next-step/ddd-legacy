package calculator.src.delimiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ColonDelimiterTest {

	@DisplayName("콜론(:)을 기준으로 문자열을 자른다.")
	@ParameterizedTest
	@MethodSource("parametersForTokenizeTest")
	void tokenize_tokenizingByComma(String input, List<String> expected) {

		// given
		var colonDelimiter = new ColonDelimiter();

		// when
		var actual = colonDelimiter.tokenize(input);

		// then
		assertThat(actual).isEqualTo(expected);
	}

	private static Stream<Arguments> parametersForTokenizeTest() {
		return Stream.of(
			Arguments.of("1:2", List.of("1", "2")),
			Arguments.of("1:2:3:4", List.of("1", "2", "3", "4")),
			Arguments.of("1: 2:  3", List.of("1", "2", "3")),
			Arguments.of("", List.of("")),
			Arguments.of("1:  4 :            2:3", List.of("1", "4", "2", "3")),
			Arguments.of("1:  4;'/ :            2:3", List.of("1", "4;'/", "2", "3"))
		);
	}
}