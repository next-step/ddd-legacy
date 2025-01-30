package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumbersTest {

	@DisplayName("입력된 0이상 숫자 문자열로 총합을 구할 수 있다.")
	@Test
	void positiveNumbers() {
		// given
		List<PositiveNumber> positiveNumberList =
			List.of(
				new PositiveNumber("1"),
				new PositiveNumber("10"),
				new PositiveNumber("0"),
				new PositiveNumber("3"));
		PositiveNumbers positiveNumbers = new PositiveNumbers(positiveNumberList);
		int expectedSum = 14;

		// when
		int actualSum = positiveNumbers.getSum();

		// then
		assertThat(actualSum).isEqualTo(expectedSum);
	}
}
