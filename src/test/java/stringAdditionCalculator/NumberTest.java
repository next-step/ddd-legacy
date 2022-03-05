package stringAdditionCalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NumberTest {

	@DisplayName("입력 값이 float(or double) 형이면 에러가 발생한다")
	@Test
	void float_or_double_is_input_then_error_occurred() {
		String floatNumberString = "12.34";

		assertThatThrownBy(() -> new Number(floatNumberString)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("입력 값이 100을 넘어가면 에러가 발생한다")
	@Test
	void input_exceed_range_of_integer_then_error_occurred() {
		String veryBigNumberString = "101";

		assertThatThrownBy(() -> new Number(veryBigNumberString)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("입력 값이 숫자가 아니라면 에러가 발생한다")
	@Test
	void not_number_is_input_then_error_occurred() {
		String notNumber = "a";

		assertThatThrownBy(() -> new Number(notNumber)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("null을 입력할 경우 0을 반환한다")
	@Test
	void null_is_input_then_return_zero() {
		String nullString = null;
		Number number = new Number(nullString);

		assertThat(number.getNumber()).isZero();
		assertThat(number).isEqualTo(new Number("0"));
	}

	@DisplayName("빈 문자열을 입력할 경우 0을 반환한다")
	@Test
	void empty_string_is_input_then_return_zero() {
		String emptyString = "";
		Number number = new Number(emptyString);

		assertThat(number.getNumber()).isZero();
		assertThat(number).isEqualTo(new Number("0"));
	}
}
