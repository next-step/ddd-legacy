package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.ProductFixture;

public class OrderServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private OrderService orderService;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private MenuGroupRepository menuGroupRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderTableRepository orderTableRepository;

	@DisplayName("주문")
	@Test
	void 주문() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));
		OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.sat());

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(19000));
		orderLineItemRequest.setQuantity(3);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.EAT_IN);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));
		givenRequest.setOrderTableId(givenOrderTable.getId());

		// when
		Order givenOrder = orderService.create(givenRequest);

		// then
		Assertions.assertThat(givenOrder.getId()).isNotNull();
		Assertions.assertThat(givenOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
	}

	@DisplayName("주문 실패 : 종류 없음")
	@Test
	void 주문_실패_1() {
		// given
		Order givenRequest = new Order();
		givenRequest.setType(null); // empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 주문 항목 없음")
	@Test
	void 주문_실패_2() {
		// given
		Order givenRequest = new Order();
		givenRequest.setType(OrderType.EAT_IN);
		givenRequest.setOrderLineItems(Collections.emptyList()); // empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 매장에서 식사가 아닌데, 주문 항목의 수량은 0 보다 작음")
	@Test
	void 주문_실패_3() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(19000));
		orderLineItemRequest.setQuantity(-1);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.DELIVERY);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 주문 항목에 해당하는 메뉴가 모두 전시 상태가 아님")
	@Test
	void 주문_실패_4() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.hidden(new BigDecimal(19000), givenMenuGroup, givenProduct));

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(19000));
		orderLineItemRequest.setQuantity(3);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.EAT_IN);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 주문 항목에 해당하는 메뉴가 모두 전시 상태가 아님")
	@Test
	void 주문_실패_5() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(100)); // not equal to menu's price
		orderLineItemRequest.setQuantity(3);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.EAT_IN);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 배달인데, 배달 주소가 빈 값")
	@Test
	void 주문_실패_6() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(19000));
		orderLineItemRequest.setQuantity(3);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.DELIVERY);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));
		givenRequest.setDeliveryAddress(""); // empty

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 매장에서 식사인데, 주문 테이블이 없음")
	@Test
	void 주문_실패_7() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(19000));
		orderLineItemRequest.setQuantity(3);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.EAT_IN);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));
		givenRequest.setOrderTableId(UUID.randomUUID()); // unknown

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(throwingCallable);
	}

	@DisplayName("주문 실패 : 매장에서 식사인데, 주문 테이블이 비어있음")
	@Test
	void 주문_실패_8() {
		// given
		Product givenProduct = productRepository.save(ProductFixture.product(new BigDecimal(17000)));
		MenuGroup givenMenuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup());
		Menu givenMenu = menuRepository.save(MenuFixture.menu(new BigDecimal(19000), givenMenuGroup, givenProduct));
		OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.orderTable());

		OrderLineItem orderLineItemRequest = new OrderLineItem();
		orderLineItemRequest.setMenuId(givenMenu.getId());
		orderLineItemRequest.setPrice(new BigDecimal(19000));
		orderLineItemRequest.setQuantity(3);

		Order givenRequest = new Order();
		givenRequest.setType(OrderType.EAT_IN);
		givenRequest.setOrderLineItems(Collections.singletonList(orderLineItemRequest));
		givenRequest.setOrderTableId(givenOrderTable.getId());

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(givenRequest);

		// then
		Assertions.assertThatIllegalStateException().isThrownBy(throwingCallable);
	}
}
