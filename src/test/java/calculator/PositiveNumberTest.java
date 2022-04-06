package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {

	@DisplayName(value = "PositiveNumber의 값은 0미만이다.")
	@Test
	void constructor1() {
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new PositiveNumber(-1));
	}

	@DisplayName(value = "PositiveNumber의 값은 0이상이다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 1})
	void constructor2(int number) {
		PositiveNumber actual = new PositiveNumber(number);

		assertThat(actual.value()).isEqualTo(number);
	}

	@DisplayName("두개의 PositiveNumber를 더할수 있다.")
	@Test
	void plus() {
		PositiveNumber one = new PositiveNumber(1);
		PositiveNumber two = new PositiveNumber(2);

		assertThat(one.plus(two)).isEqualTo(new PositiveNumber(3));
	}
}
