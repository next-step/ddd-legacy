package calculator;

import static org.assertj.core.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculationSpecTest {

	@DisplayName("숫자가 아닌 입력 값을 정규표현식으로 판별한다")
	@ParameterizedTest
	@ValueSource(strings = { "-123", "123 456", "coffee" })
	void filterNotNumbers(String input) {
		assertThat(isNotPositiveNumber(input)).isTrue();
	}

	private boolean isNotPositiveNumber(String input) {
		final Pattern numberRegexp = Pattern.compile("^\\d+$");
		return !numberRegexp.matcher(input).matches();
	}
}