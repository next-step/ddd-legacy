package kitchenpos.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kitchenpos.stub.ProductStub;

class ProductTest {

	@DisplayName("상품은 전체 프로젝트에서 추적 가능한 `유일한 식별자`를 지녀야 한다.")
	@Test
	void uuid() {
		// given
		int numberOfProducts = 1000;

		// when
		Set<UUID> productIds = IntStream.range(0, numberOfProducts)
			.mapToObj(i -> ProductStub.createDefault().getId())
			.collect(Collectors.toSet());

		// then
		assertThat(productIds.size()).isEqualTo(numberOfProducts);
	}
}
