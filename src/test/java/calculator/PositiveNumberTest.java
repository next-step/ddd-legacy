package calculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PositiveNumberTest {

	@DisplayName("음수 문자열을 전달하면 RuntimeException 예외 처리를 한다.")
	@ParameterizedTest
	@ValueSource(strings = {"-1"})
	void throwRuntimeExceptionWhenNegativeNumber(final String text) {
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> new PositiveNumber(text));
	}
}
