package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTableTest {

	@DisplayName("주문테이블 고객 수를 변경할 수 있다")
	@Test
	void changeNumberOfGuests() {
		// given
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(0);

		int changedNumberOfGuests = 2;

		// when
		orderTable.setNumberOfGuests(changedNumberOfGuests);

		// then
		assertThat(orderTable.getNumberOfGuests()).isEqualTo(changedNumberOfGuests);
	}
}
