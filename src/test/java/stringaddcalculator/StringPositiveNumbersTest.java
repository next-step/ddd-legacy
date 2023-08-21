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
class StringPositiveNumbersTest {

	@DisplayName("생성 테스트")
	@Test
	void createTest() {
		assertThatCode(() -> StringPositiveNumbers.fromStringCollection(List.of(
			"1", "2", "3"))
		)
			.doesNotThrowAnyException();
	}

	@DisplayName("숫자 문자 컬렉션의 합 반환 테스트")
	@ParameterizedTest
	@MethodSource
	void sumTest(List<String> numbers, int expected) {
		StringPositiveNumbers stringPositiveNumbers = StringPositiveNumbers.fromStringCollection(numbers);
		assertThat(stringPositiveNumbers.sum())
			.isEqualTo(expected);
	}

	private static Stream<Arguments> sumTest() {
		return Stream.of(
			Arguments.of(
				List.of("1", "2", "3"), 6),
			Arguments.of(
				List.of("1", "2", "3", "4", "5"), 15),
			Arguments.of(
				List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), 55),
			Arguments.of(
				List.of(
					"1",
					"2",
					"3",
					"4",
					"5",
					"6",
					"7",
					"8",
					"9",
					"10",
					"11",
					"12",
					"13",
					"14",
					"15",
					"16",
					"17",
					"18",
					"19",
					"20"),
				210),
			Arguments.of(List.of("1"), 1)
		);
	}
}
