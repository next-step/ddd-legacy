package stringcalculator;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class NumbersTest {

	@DisplayName(value = "숫자가 빈값이나 Null 일 때 0 반환")
	@ParameterizedTest
	@NullAndEmptySource
	void emptyOrNullTest(final String inputNumber) {
		Numbers numbers = new Numbers(inputNumber);
		Assertions.assertThat(numbers.firstNumber()).isEqualTo(0);
	}

	@DisplayName(value = "덧셈이 가능한지 체크")
	@ParameterizedTest
	@ValueSource(strings = {"//;\n1;2;3"})
	void customDelimiter(final String text) {
		assertThat(new Numbers(text).isSummable()).isTrue();
	}

}
