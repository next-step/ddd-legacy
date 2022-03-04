package calculation.test;

import calculation.convert.Delimiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DelimiterTest {

	@ParameterizedTest(name = "비교 {index} [{arguments}]")
	@DisplayName("Delimiter을 이용한 문자열 분리")
	@MethodSource
	void add(String stringNumbers, String[] expected) {
		//when
		String[] actual = Delimiter.separateUsingDelimiter(stringNumbers);
		//then
		assertThat(actual).hasSameElementsAs(Arrays.asList(expected));
	}

	private static Stream<Arguments> add() {
		return Stream.of(
			Arguments.of("1", new String[]{"1"}),
			Arguments.of("1,2", new String[]{"1", "2"}),
			Arguments.of("1,2:3", new String[]{"1", "2", "3"}),
			Arguments.of("//;\n1;2;3", new String[]{"1", "2", "3"})
		);
	}

}
