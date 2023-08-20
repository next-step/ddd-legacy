package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("숫자 문자 컬렉션 테스트")
class StringNumbersTest {

	@DisplayName("생성 테스트")
	@Test
	void createTest() {
		assertThatCode(() -> StringNumbers.from(List.of(
			StringNumber.from("1"),
			StringNumber.from("2"),
			StringNumber.from("3")))
		)
			.doesNotThrowAnyException();
	}

	@DisplayName("숫자 문자 컬렉션의 합 반환 테스트")
	@ParameterizedTest
	@MethodSource
	void sumTest(List<StringNumber> numbers, int expected) {
		StringNumbers stringNumbers = StringNumbers.from(numbers);
		assertThat(stringNumbers.sum())
			.isEqualTo(expected);
	}

	private static Stream<Arguments> sumTest() {
		return Stream.of(
			Arguments.of(
				List.of(
					StringNumber.from("1"),
					StringNumber.from("2"),
					StringNumber.from("3")),
				6),
			Arguments.of(
				List.of(
					StringNumber.from("1"),
					StringNumber.from("2"),
					StringNumber.from("3"),
					StringNumber.from("4"),
					StringNumber.from("5")),
				15),
			Arguments.of(
				List.of(
					StringNumber.from("1"),
					StringNumber.from("2"),
					StringNumber.from("3"),
					StringNumber.from("4"),
					StringNumber.from("5"),
					StringNumber.from("6"),
					StringNumber.from("7"),
					StringNumber.from("8"),
					StringNumber.from("9"),
					StringNumber.from("10")),
				55),
			Arguments.of(
				List.of(
					StringNumber.from("1"),
					StringNumber.from("2"),
					StringNumber.from("3"),
					StringNumber.from("4"),
					StringNumber.from("5"),
					StringNumber.from("6"),
					StringNumber.from("7"),
					StringNumber.from("8"),
					StringNumber.from("9"),
					StringNumber.from("10"),
					StringNumber.from("11"),
					StringNumber.from("12"),
					StringNumber.from("13"),
					StringNumber.from("14"),
					StringNumber.from("15"),
					StringNumber.from("16"),
					StringNumber.from("17"),
					StringNumber.from("18"),
					StringNumber.from("19"),
					StringNumber.from("20")),
				210),
			Arguments.of(
				List.of(
					StringNumber.from("1")
				),
				1)
		);

	}
}
