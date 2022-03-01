package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveOrZeroNumberTest {

	@DisplayName("0이하의 숫자로는 객체 생성 시 예외 발생한다.")
	@ParameterizedTest
	@ValueSource(ints = {-1, -100})
	void createNegative(int value) {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new PositiveOrZeroNumber(value));
	}

	@DisplayName("0 이상의 숫자로 객체를 생성할 수 있다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 1})
	void createZeroOrPositive(int value) {
		PositiveOrZeroNumber result = new PositiveOrZeroNumber(value);
		assertThat(result).isNotNull();
	}

	@DisplayName("덧셈이 가능하다.")
	@ParameterizedTest
	@CsvSource(value = {"1,2,3", "10,2,12"})
	void plus(int x, int y, int expectedInt) {
		PositiveOrZeroNumber expected = new PositiveOrZeroNumber(expectedInt);
		PositiveOrZeroNumber result = new PositiveOrZeroNumber(x).plus(new PositiveOrZeroNumber(y));
		assertThat(result).isEqualTo(expected);
	}
}
