package stringcalculator;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class NumberTest {

	@DisplayName(value = "숫자 생성 테스트")
	@ParameterizedTest
	@ValueSource(strings = {"1", "2", "3"})
	void numberTest(final String inputNumber) {
		Number number = new Number(inputNumber);
		Assertions.assertThat(number).isEqualTo(new Number(inputNumber));
	}

	@DisplayName(value = "Number 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
	@Test
	void negative() {
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> new Number("-5"));
	}

	@DisplayName(value = "Number 숫자가 아닌 문자열을 전달하는 경우 RuntimeException 예외 처리를 한다.")
	@Test
	void string() {
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> new Number("ab"));
	}
}
