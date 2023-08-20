package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("문자열 구분기 테스트")
class StringSeparatorTest {

	@DisplayName("문자열 구분기 생성 테스트")
	@Test
	void createTest() {
		assertThatCode(() -> StringSeparator.from("test"))
			.doesNotThrowAnyException();
	}

	@DisplayName("기본 구분자 중 콤마를 통해 구분된 문자열 리스트 반환 테스트")
	@ParameterizedTest
	@CsvSource(value = {"1,2,3:1,2,3", "1,2:1,2", "1:1"}, delimiter = ':')
	void splitByDefaultDelimiterTest(String target, String expected) {
		StringSeparator stringSeparator = StringSeparator.from(target);

		assertThat(stringSeparator.split())
			.containsExactly(expected.split(","));
	}

	@DisplayName("기본 구분자 중 콜론을 통해 구분된 문자열 리스트 반환 테스트")
	@ParameterizedTest
	@CsvSource(value = {"1:2:3|1,2,3", "1:2|1,2", "1:1"}, delimiter = '|')
	void splitByColonDelimiterTest(String target, String expected) {
		StringSeparator stringSeparator = StringSeparator.from(target);

		assertThat(stringSeparator.split())
			.containsExactly(expected.split(","));
	}

	@DisplayName("커스텀 구분자를 통해 구분된 문자열 리스트 반환 테스트")
	@ParameterizedTest
	@ValueSource(strings = {"//;\n1;2;3", "//!\n1!2!3", "//@\n1@2@3"})
	void splitByCustomDelimiterTest(String target) {
		StringSeparator stringSeparator = StringSeparator.from(target);

		List<String> split = stringSeparator.split();
		assertThat(split)
			.containsExactly("1", "2", "3");
	}

	@DisplayName("한 개의 숫자를 입력할 경우 해당 숫자만 갖는 리스트 반환 테스트")
	@ParameterizedTest
	@ValueSource(strings = {"1", "2", "3"})
	void splitOnlyOneNumberTest(String target) {
		StringSeparator stringSeparator = StringSeparator.from(target);

		assertThat(stringSeparator.split())
			.containsExactly(target);
	}

}
