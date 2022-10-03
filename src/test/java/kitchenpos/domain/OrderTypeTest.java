package kitchenpos.domain;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTypeTest {

	@DisplayName("주문은 배달, 테이크아웃, 홀 타입으로 나누어 진다. (DELIVERY, TAKEOUT, EAT_IN)")
	@Test
	void values() {
		List<String> names = Arrays.stream(OrderType.values())
			.map(Enum::name)
			.collect(toList());

		assertThat(names).containsExactlyInAnyOrder("DELIVERY", "TAKEOUT", "EAT_IN");
	}
}
