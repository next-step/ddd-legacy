package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;

public class OrderTableServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private OrderTableService orderTableService;
	@Autowired
	private OrderTableRepository orderTableRepository;

	@DisplayName("주문 테이블 생성")
	@Test
	void 주문_테이블_생성() {
		// given
		OrderTable request = new OrderTable();
		request.setName("9번");

		// when
		OrderTable orderTable = orderTableService.create(request);

		// then
		assertThat(orderTable.getId()).isNotNull();
		assertThat(orderTable.getName()).isEqualTo("9번");
		assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
		assertThat(orderTable.isEmpty()).isEqualTo(true);
	}

	@DisplayName("주문 테이블 생성 실패 : 이름 없음")
	@Test
	void 주문_테이블_생성_실패_1() {
		// given
		OrderTable request = new OrderTable();
		request.setName("");

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.create(request);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 테이블 착석")
	@Test
	void 주문_테이블_착석() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.orderTable());

		// when
		OrderTable actual = orderTableService.sit(given.getId());

		// then
		assertThat(actual.getId()).isEqualTo(given.getId());
		assertThat(actual.isEmpty()).isEqualTo(false);
	}

	@DisplayName("주문 테이블 비우기")
	@Test
	void 주문_테이블_비우기() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.orderTable());

		// when
		OrderTable actual = orderTableService.clear(given.getId());

		// then
		assertThat(actual.getId()).isEqualTo(given.getId());
		assertThat(actual.getNumberOfGuests()).isEqualTo(0);
		assertThat(actual.isEmpty()).isEqualTo(true);
	}

	@DisplayName("주문 테이블 손님 수 변경")
	@Test
	void 주문_테이블_손님_수_변경() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.sit());
		OrderTable request = new OrderTable();
		request.setNumberOfGuests(4);

		// when
		OrderTable actual = orderTableService.changeNumberOfGuests(given.getId(), request);

		// then
		assertThat(actual.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
	}

	@DisplayName("주문 테이블 손님 수 변경 실패 : 손님 수 음수")
	@Test
	void 주문_테이블_손님_수_변경_실패_1() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.sit());
		OrderTable request = new OrderTable();
		request.setNumberOfGuests(-1);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> orderTableService.changeNumberOfGuests(given.getId(), request);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 테이블 손님 수 변경 실패 : 빈 테이블")
	@Test
	void 주문_테이블_손님_수_변경_실패_2() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.orderTable());
		OrderTable request = new OrderTable();
		request.setNumberOfGuests(-1);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> orderTableService.changeNumberOfGuests(given.getId(), request);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 주문 조회")
	@Test
	void 전체_주문_조회() {
		// given
		orderTableRepository.save(OrderTableFixture.orderTable());

		// when
		List<OrderTable> actual = orderTableService.findAll();

		// then
		assertThat(actual).isNotEmpty();
	}
}
