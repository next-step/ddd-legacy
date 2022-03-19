package kitchenpos.study;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class BigDecimalTest {

	@Test
	void compare_to_test() {
		BigDecimal oneHundredBigDecimal1 = new BigDecimal(100);
		BigDecimal twoHundredBigDecimal = new BigDecimal(200);
		BigDecimal oneHundredBigDecimal2 = new BigDecimal(100);

		Assertions.assertThat(oneHundredBigDecimal1.compareTo(twoHundredBigDecimal) < 0).isTrue();
		Assertions.assertThat(oneHundredBigDecimal1.compareTo(oneHundredBigDecimal2) == 0).isTrue();
		Assertions.assertThat(oneHundredBigDecimal1.compareTo(BigDecimal.ZERO) > 0).isTrue();
	}
}
