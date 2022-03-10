package stringAdditionCalculator;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NumbersTest {
	@DisplayName("100개 이상의 값을 추가하면 에러가 발생한다")
	@Test
	void insert_above_100_values_then_error_occurred() {
		List<String> stringNumbersOverOneHundred = new ArrayList<>();
		IntStream.range(1, 103).forEach(i -> stringNumbersOverOneHundred.add("1"));

		assertThatThrownBy(() -> new Numbers(stringNumbersOverOneHundred.toArray(new String[0]))).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("목록에 있는 값들의 합을 반환한다")
	@Test
	void when_get_sum_method_is_called_return_sum_of_number_list() {
		String[] stringNumbers = {"1", "2"};
		Numbers numbers = new Numbers(stringNumbers);

		assertThat(numbers.getSum()).isEqualTo(3);
	}
}
