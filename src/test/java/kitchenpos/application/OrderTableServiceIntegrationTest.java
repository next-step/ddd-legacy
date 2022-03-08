package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceIntegrationTest extends IntegrationTest {
	private static final String ORDER_TABLE_NAME = "1번";
	private static final int NUMBER_OF_GUESTS_ZERO = 0;
	private static final int NUMBER_OF_GUESTS_ONE = 1;
	private static final int NUMBER_OF_GUESTS_NEGATIVE = -1;
	private static final BigDecimal MENU_PRICE = new BigDecimal(20000);
	private static final BigDecimal PRODUCT_PRICE = new BigDecimal(15000);

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
	void createOrderTable() {
		// given
		OrderTable orderTable = new OrderTable();
		orderTable.setName(ORDER_TABLE_NAME);

		// when
		OrderTable actualOrderTable = orderTableService.create(orderTable);

		// then
		assertAll(
			() -> assertThat(actualOrderTable.getId()).isNotNull(),
			() -> assertThat(actualOrderTable.getName()).isEqualTo(ORDER_TABLE_NAME),
			() -> assertThat(actualOrderTable.getNumberOfGuests()).isEqualTo(NUMBER_OF_GUESTS_ZERO),
			() -> assertThat(actualOrderTable.isEmpty()).isTrue()
		);
	}

	@DisplayName("이름이 null이거나 empty이면 주문 테이블 생성 실패")
	@ParameterizedTest
	@NullAndEmptySource
	void failCreatingOrderTableWhenNameIsNullOrEmpty(String name) {
		// given
		OrderTable orderTable = new OrderTable();
		orderTable.setName(name); // null or empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.create(orderTable);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 테이블 착석")
	@Test
	void sitOrderTable() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());

		// when
		OrderTable actualOrderTable = orderTableService.sit(orderTable.getId());

		// then
		assertThat(actualOrderTable.getId()).isEqualTo(orderTable.getId());
		assertThat(actualOrderTable.isEmpty()).isFalse();
	}

	@DisplayName("주문 테이블 비우기")
	@Test
	void clearOrderTable() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());

		// when
		OrderTable actualOrderTable = orderTableService.clear(orderTable.getId());

		// then
		assertThat(actualOrderTable.getId()).isEqualTo(orderTable.getId());
		assertThat(actualOrderTable.getNumberOfGuests()).isEqualTo(NUMBER_OF_GUESTS_ZERO);
		assertThat(actualOrderTable.isEmpty()).isTrue();
	}

	@DisplayName("주문상태가 완료가아니면 주문 테이블 비우기 실패")
	@Test
	void failEmptyOrderTableWhenOrderIsNotCompleted() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());
		Product product = productRepository.save(ProductFixture.PRODUCT(PRODUCT_PRICE));
		MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());
		Menu menu = menuRepository.save(MenuFixture.DISPLAYED_MENU(MENU_PRICE, menuGroup, product));
		orderRepository.save(OrderFixture.SERVED_EAT_IN_ORDER(menu, orderTable));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.clear(orderTable.getId());

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 테이블 손님 수 변경")
	@Test
	void changingOrderTable() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.SAT_ORDER_TABLE());
		OrderTable newOrderTable = new OrderTable();
		newOrderTable.setNumberOfGuests(NUMBER_OF_GUESTS_ONE);

		// when
		OrderTable actual = orderTableService.changeNumberOfGuests(orderTable.getId(), newOrderTable);

		// then
		assertThat(actual.getNumberOfGuests()).isEqualTo(newOrderTable.getNumberOfGuests());
	}

	@DisplayName("손님수가 음수이면 주문 테이블 손님 수 변경 실패")
	@Test
	void failChangingOrderTableWhenHumanIsNegative() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.SAT_ORDER_TABLE());
		OrderTable newOrderTable = new OrderTable();
		newOrderTable.setNumberOfGuests(NUMBER_OF_GUESTS_NEGATIVE);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> orderTableService.changeNumberOfGuests(orderTable.getId(), newOrderTable);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("빈 테이블이면 주문 테이블 손님 수 변경 실패")
	@Test
	void failChangingOrderTableWhenTableIsEmpty() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());
		OrderTable newOrderTable = new OrderTable();
		newOrderTable.setNumberOfGuests(NUMBER_OF_GUESTS_ONE);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable =
			() -> orderTableService.changeNumberOfGuests(orderTable.getId(), newOrderTable);

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 주문 테이블 조회")
	@Test
	void readAllOrderTable() {
		// given
		OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_ORDER_TABLE());

		// when
		List<OrderTable> orderTables = orderTableService.findAll();

		// then
		List<UUID> actualIds = orderTables.stream().map(OrderTable::getId).collect(Collectors.toList());

		assertAll(
			() -> Assertions.assertThat(orderTables).isNotEmpty(),
			() -> Assertions.assertThat(actualIds).contains(orderTable.getId())
		);
	}
}
