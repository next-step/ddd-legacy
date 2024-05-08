package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {


	@DisplayName("sum 메소드는 숫자들의 합을 반환한다.")
	@Test
	void sum() {
		assertThat(new Numbers(List.of(new NonNegativeNumber(1), new NonNegativeNumber(5))).sum())
			.isEqualTo(6);
	}
}
