package stringAdditionCalculator;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NumbersTest {
	@DisplayName("100개 이상의 값을 추가하면 에러가 발생한다")
	@Test
	void insert_above_100_values_then_error_occurred() {
		Numbers numbers = new Numbers();

		assertThatThrownBy(() -> IntStream.range(1, 102)
			.forEach(i -> numbers.addNumber(new Number("1")))).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("목록이 비었을 경우 0을 반환한다")
	@Test
	void when_get_Sum_method_is_called_and_list_is_empty_then_return_zero() {
		Numbers numbers = new Numbers();

		assertThat(numbers.getSum()).isZero();
	}

	@DisplayName("목록에 있는 값들의 합을 반환한다")
	@Test
	void when_get_sum_method_is_called_return_sum_of_number_list() {
		String[] stringNumbers = {"1", "2"};
		Numbers numbers = new Numbers(stringNumbers);

		assertThat(numbers.getSum()).isEqualTo(3);
	}
}
