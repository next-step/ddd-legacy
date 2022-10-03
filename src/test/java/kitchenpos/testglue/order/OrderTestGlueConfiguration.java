package kitchenpos.testglue.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.OrderService;
import kitchenpos.application.fixture.OrderLineItemMother;
import kitchenpos.application.fixture.OrderMother;
import kitchenpos.configuration.KitchenridersClientConfiguration.FakeKitchenridersClient;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;
import kitchenpos.util.testglue.test.TestGlueResponse;

@TestGlueConfiguration
public class OrderTestGlueConfiguration extends TestGlueSupport {

	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final KitchenridersClient kitchenridersClient;

	public OrderTestGlueConfiguration(
		OrderService orderService,
		OrderRepository orderRepository,
		KitchenridersClient kitchenridersClient
	) {
		this.orderService = orderService;
		this.orderRepository = orderRepository;
		this.kitchenridersClient = kitchenridersClient;
	}

	@TestGlueOperation("{} 메뉴 {} 개로 {} 주문메뉴정보를 생성하고")
	public void createOrderLineItem(String menuName, String quantity, String orderLineItemName) {
		Menu menu = getAsType(menuName, Menu.class);

		OrderLineItem orderLineItem = OrderLineItemMother.create(menu.getId(), menu.getPrice(), Integer.parseInt(quantity));

		List<OrderLineItem> orderLineItems = getAsType(orderLineItemName, List.class);
		if (orderLineItems == null) {
			put(orderLineItemName, new ArrayList<>());
		}
		orderLineItems = getAsType(orderLineItemName, List.class);

		orderLineItems.add(orderLineItem);
	}

	@TestGlueOperation("{} 주문메뉴정보를 가격을 {}로 변경하고")
	public void createOrderLineItem_setPrice(String orderLineItemName, String price) {
		List<OrderLineItem> orderLineItem = getAsType(orderLineItemName, List.class);
		orderLineItem.forEach(v -> v.setPrice(BigDecimal.valueOf(Long.parseLong(price))));
	}

	@TestGlueOperation("{} 주문 테이블과 {} 주문메뉴 정보로 {} 주문을 생성하면")
	public void createOrderTable(String orderTableName, String orderLineItemsName, String orderName) {
		List<OrderLineItem> orderLineItems = getAsType(orderLineItemsName, List.class);
		var orderTable = getAsType(orderTableName, OrderTable.class);

		Order order = OrderMother
			.findCreatorByName(orderName)
			.create(orderLineItems, orderTable.getId());

		TestGlueResponse<Order> response = createResponse(() -> orderService.create(order));

		put("orderResponse", response);
	}

	@TestGlueOperation("{} 주문메뉴 정보로 {} 주문을 생성하면")
	public void createOrderTable_noOrderTable(String orderLineItemsName, String orderName) {
		createOrderTable(UUID.randomUUID().toString(), orderLineItemsName, orderName);
	}

	@TestGlueOperation("{} 주문 테이블과 {} 주문메뉴 정보로 {} 주문을 생성하고")
	public void createOrderTable2(String orderTableName, String orderLineItemsName, String orderName) {
		List<OrderLineItem> orderLineItems = getAsType(orderLineItemsName, List.class);
		var orderTable = getAsType(orderTableName, OrderTable.class);

		Order order = OrderMother
			.findCreatorByName(orderName)
			.create(orderLineItems, orderTable == null ? null : orderTable.getId());

		put(orderName, orderService.create(order));
	}

	@TestGlueOperation("{} 주문메뉴 정보로 {} 주문을 생성하고")
	public void createOrderTable_noOrderTable2(String orderLineItemsName, String orderName) {
		createOrderTable2(UUID.randomUUID().toString(), orderLineItemsName, orderName);
	}

	@TestGlueOperation("주문이 생성된다")
	public void createOrderResponse() {
		TestGlueResponse<Order> response = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isTrue();

		assertThat(orderRepository.findById(response.getData().getId())).isNotEmpty();
	}

	@TestGlueOperation("주문이 실패한다")
	public void createOrderResponse_fail() {
		TestGlueResponse<Order> response = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}

	@TestGlueOperation("주문의 상태는 {} 이다")
	public void orderStatus(String status) {
		OrderStatus orderStatus = OrderStatus.valueOf(status);

		TestGlueResponse<Order> response = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isTrue();
		assertThat(response.getData().getStatus()).isEqualTo(orderStatus);
	}

	@TestGlueOperation("{} 주문을 수락하면")
	public void accept(String orderName) {
		Order order = getAsType(orderName, Order.class);

		TestGlueResponse<Order> response = createResponse(() -> orderService.accept(order.getId()));

		put("orderResponse", response);
	}

	@TestGlueOperation("{} 주문을 수락하고")
	public void accept2(String orderName) {
		Order order = getAsType(orderName, Order.class);

		put(orderName, orderService.accept(order.getId()));
	}

	@TestGlueOperation("주문 수락에 실패한다")
	public void accept_fail() {
		TestGlueResponse<Order> orderResponse = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(orderResponse.isOk()).isFalse();
	}

	@TestGlueOperation("주문 수락에 실패한다")
	public void accept_deliverySystem() {
		TestGlueResponse<Order> orderResponse = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(orderResponse.isOk()).isFalse();
	}

	@TestGlueOperation("배달 요청을 전송한다")
	public void accept_requestToRiderSystem() {
		FakeKitchenridersClient kitchenridersClient = (FakeKitchenridersClient) this.kitchenridersClient;

		assertThat(kitchenridersClient.getRequestCount() >= 1).isTrue();
	}

	@TestGlueOperation("{} 주문을 서빙하면")
	public void serve(String orderName) {
		Order order = getAsType(orderName, Order.class);

		TestGlueResponse<Order> response = createResponse(() -> orderService.serve(order.getId()));

		put("orderResponse", response);
	}

	@TestGlueOperation("{} 주문을 서빙하고")
	public void serve2(String orderName) {
		Order order = getAsType(orderName, Order.class);

		put(orderName, orderService.serve(order.getId()));
	}

	@TestGlueOperation("주문 서빙에 실패한다")
	public void serve_fail() {
		TestGlueResponse<Order> orderResponse = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(orderResponse.isOk()).isFalse();
	}

	@TestGlueOperation("{} 주문 배달을 시작하면")
	public void startDelivery(String orderName) {
		Order order = getAsType(orderName, Order.class);

		TestGlueResponse<Order> response = createResponse(() -> orderService.startDelivery(order.getId()));

		put("orderResponse", response);
	}

	@TestGlueOperation("{} 주문 배달을 시작하고")
	public void startDelivery2(String orderName) {
		Order order = getAsType(orderName, Order.class);

		put(orderName, orderService.startDelivery(order.getId()));
	}

	@TestGlueOperation("주문 배달에 실패한다")
	public void startDelivery_fail() {
		TestGlueResponse<Order> orderResponse = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(orderResponse.isOk()).isFalse();
	}

	@TestGlueOperation("{} 배달 완료가 되면")
	public void completeDelivery(String orderName) {
		Order order = getAsType(orderName, Order.class);

		TestGlueResponse<Order> response = createResponse(() -> orderService.completeDelivery(order.getId()));

		put("orderResponse", response);
	}

	@TestGlueOperation("{} 배달 완료가 되고")
	public void completeDelivery2(String orderName) {
		Order order = getAsType(orderName, Order.class);

		put(orderName, orderService.completeDelivery(order.getId()));
	}

	@TestGlueOperation("배달 완료에 실패한다")
	public void completeDelivery_fail() {
		TestGlueResponse<Order> orderResponse = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(orderResponse.isOk()).isFalse();
	}

	@TestGlueOperation("{} 주문을 종료하면")
	public void complete(String orderName) {
		Order order = getAsType(orderName, Order.class);

		TestGlueResponse<Order> response = createResponse(() -> orderService.complete(order.getId()));

		put("orderResponse", response);
	}

	@TestGlueOperation("주문 완료에 실패한다")
	public void complete_fail() {
		TestGlueResponse<Order> orderResponse = getAsType("orderResponse", TestGlueResponse.class);

		assertThat(orderResponse.isOk()).isFalse();
	}
}
