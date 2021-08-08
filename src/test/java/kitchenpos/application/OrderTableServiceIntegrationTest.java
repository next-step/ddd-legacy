package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.ProductFixture;

public class OrderTableServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private OrderTableService orderTableService;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private MenuGroupRepository menuGroupRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderTableRepository orderTableRepository;
	@Autowired
	private OrderRepository orderRepository;

	@DisplayName("주문 테이블 생성")
	@Test
	void 주문_테이블_생성() {
		// given
		OrderTable request = new OrderTable();
		request.setName("9번");

		// when
		OrderTable orderTable = orderTableService.create(request);

		// then
		assertAll(
			() -> assertNotNull(orderTable.getId()),
			() -> assertEquals("9번", orderTable.getName()),
			() -> assertEquals(0, orderTable.getNumberOfGuests()),
			() -> assertTrue(orderTable.isEmpty())
		);
	}

	@DisplayName("주문 테이블 생성 실패 : 이름 없음")
	@ParameterizedTest
	@NullAndEmptySource
	void 주문_테이블_생성_실패_1(String name) {
		// given
		OrderTable request = new OrderTable();
		request.setName(name); // null or empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.create(request);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 테이블 착석")
	@Test
	void 주문_테이블_착석() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());

		// when
		OrderTable actual = orderTableService.sit(given.getId());

		// then
		assertThat(actual.getId()).isEqualTo(given.getId());
		assertThat(actual.isEmpty()).isFalse();
	}

	@DisplayName("주문 테이블 비우기")
	@Test
	void 주문_테이블_비우기() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());

		// when
		OrderTable actual = orderTableService.clear(given.getId());

		// then
		assertThat(actual.getId()).isEqualTo(given.getId());
		assertThat(actual.getNumberOfGuests()).isEqualTo(0);
		assertThat(actual.isEmpty()).isTrue();
	}

	@DisplayName("주문 테이블 비우기 실패 : 주문 상태가 완료가 아님")
	@Test
	void 주문_테이블_비우기_실패_1() {
		// given
		OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());
		Product givenProduct = productRepository.save(ProductFixture.PRODUCT(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Menu givenMenu = menuRepository.save(
			MenuFixture.DISPLAYED_MENU(new BigDecimal(19000), givenMenuGroup, givenProduct));
		orderRepository.save(OrderFixture.SERVED_EAT_IN(givenMenu, givenOrderTable));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.clear(givenOrderTable.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 테이블 손님 수 변경")
	@Test
	void 주문_테이블_손님_수_변경() {
		// given
		OrderTable given = orderTableRepository.save(OrderTableFixture.SAT_ORDER_TABLE());
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
		OrderTable given = orderTableRepository.save(OrderTableFixture.SAT_ORDER_TABLE());
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
		OrderTable given = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());
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
		OrderTable given = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());

		// when
		List<OrderTable> actual = orderTableService.findAll();

		// then
		List<UUID> actualIds = actual.stream().map(OrderTable::getId).collect(Collectors.toList());

		assertAll(
			() -> Assertions.assertThat(actual).isNotEmpty(),
			() -> Assertions.assertThat(actualIds).contains(given.getId())
		);
	}
}
