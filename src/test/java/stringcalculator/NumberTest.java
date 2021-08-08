package stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

	@DisplayName(value = "숫자가 빈값이나 Null 일 때 0 반")
	@ParameterizedTest
	@NullAndEmptySource
	void emptyOrNullTest(final String inputNumber) {
		Number number = new Number(inputNumber);
		Assertions.assertThat(number.equals(new Number("0"))).isTrue();
	}
}
