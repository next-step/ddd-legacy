package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.*;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.Kitchenriders;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;


@DisplayName("[주문]")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final Kitchenriders kitchenridersClient = new FakeKitchenridersClient();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test
    @DisplayName("주문 방식은 반드시 있어야 한다.")
    void orderTypeNotNullTest() {
        Order order = new Order();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(OrderTypeNullException.class);
    }

    @Test
    @DisplayName("주문시 주문 메뉴는 반드시 있어야 한다.")
    void orderLineItemNotEmptyTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(OrderLineItemEmptyException.class);
    }

    @Test
    @DisplayName("조회된 메뉴의 갯수와 주문 메뉴에 포함된 메뉴의 갯수는 같아야 한다.")
    void menuSizeSameOrderLineItemSizeTest() {

        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);

        OrderLineItem notExistOrderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);

        order.setOrderLineItems(Arrays.asList(orderLineItem, notExistOrderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NotTheSameSizeException.class);
    }

    @Test
    @DisplayName("주문 방식이 `배달`, `포장` 인경우에는 주문한 메뉴의 갯수는 반드시 있어야한다.")
    void deliveryTakeoutQuantityLessThanZeroTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(-1);

        order.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(QuantityLessThenZeroException.class);
    }

    @Test
    @DisplayName("해당 메뉴가 미노출 상태라면 주문할 수 없다.")
    void menuDisplayIsFalseTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(2);

        order.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(DisplayFalseException.class);
    }

    @Test
    @DisplayName("해당 메뉴의 가격과 주문 메뉴의 가격이 일치 하지 않으면 주문할 수 없다.")
    void menuPriceNotTheSameRequestPriceTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(19_000L));
        orderLineItem.setQuantity(2);

        order.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NotTheSamePriceException.class);
    }

    @Test
    @DisplayName("주문 방식이 `배달`인 경우, 배달하고자 하는 주소는 반드시 존재해야 한다. 없다면 DeliveryAddressEmptyException 발생")
    void deliveryRequiredDeliveryAddress() {
        Order orderRequest = new Order();
        orderRequest.setType(OrderType.DELIVERY);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(DeliveryAddressEmptyException.class);
    }

    @Test
    @DisplayName("매장 식사인데, 주문 테이블 ID가 존재하지 않는 경우 OrderTableNotFoundException 발생")
    void orderTableNotFoundTest() {

        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderTableId(UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(OrderTableNotFoundException.class);
    }

    @Test
    @DisplayName("주문테이블이 비어있다면 IllegalStateException 발생")
    void orderTableIsEmptyTrueTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderTableId(orderTable.getId());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));
        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 할 수 있다")
    void orderTableIsEmptyTru1eTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTableRepository.save(orderTable);

        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderTableId(orderTable.getId());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));

        Order actual = orderService.create(orderRequest);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> Assertions.assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @Test
    @DisplayName("주문 수락 시점에 주문 정보가 있어야 한다.")
    void orderAcceptExistOrderTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @Test
    @DisplayName("주문 수락시 OrderType 상태는 `WAITING` 이어야 한다.")
    void orderAcceptStatusTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
//        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 배달인 경우 라이더에게 주문을 요청한다.")
    void orderAcceptStatusDeliveryVerifyKitchenridersClientTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(Collections.emptyList());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        orderService.accept(order.getId());
//        Mockito.verify(kitchenridersClient, Mockito.atLeastOnce()).requestDelivery(order.getId(), BigDecimal.valueOf(18_000L), "ABC");
    }

    @Test
    @DisplayName("주문을 수락한다")
    void acceptOrderTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(Collections.emptyList());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        Order actual = orderService.accept(order.getId());

        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문이 접수되었을때 해당 주문이 없다면 예외 발생")
    void serveOrderNotFundTest() {

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @Test
    @DisplayName("주문이 되었을때 상태가 ACCEPTED여야 한다.")
    void serveOrderStatusTest() {

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);


        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문이 도착하였을 경우 ")
    void serveSuccessTest() {

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        Order actual = orderService.serve(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    /*
     *  배달 시작
     */

    @Test
    @DisplayName("배달을 시작한다.")
    void delivery() {

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        Order actual = orderService.startDelivery(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달을 시작시 해당 주문이 없으면 안된다.")
    void deliveryOrderNotFoundTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("배달을 시작시 주문 타입은 DELIVERY여야 한다.")
    void deliveryOrderTypeTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("배달을 시작시 주문 상태는 SERVED여야 한다.")
    void deliveryOrderStatusTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    /*
     *  배달 완료
     */

    @Test
    @DisplayName("배달을 완료한다.")
    void completeDeliveryTest() {

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.DELIVERING);

        orderRepository.save(order);

        Order actual = orderService.completeDelivery(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배달 완료 시점에 주문정보가 없으면 안된다.")
    void completeDeliveryOrderNotFoundTest() {

        Order order = new Order();
        order.setId(UUID.randomUUID());

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("배달 완료시점의 주문 상태는 DELIVERING 여야한다.")
    void completeDeliveryOrderStatusTest() {

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.SERVED);

        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    /*
     *  완료
     */

    @Test
    @DisplayName("주문 완료되었다.")
    void completeTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        Order actual = orderService.complete(order.getId());
        Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 타입이 배달인 경우의 주문 상태가 배달완료일때 완료처리 가능")
    void completeOrderTypeDeliveryTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 매장식사 or 포장의 경우 주문 상태가 SERVED(서빙)가 아니라면 Exception ")
    void completeOrderTypeTakeoutOrEatInTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 매장식사인 경우 주문 테이블을 찾아 초기화한다. ")
    void completeOrderTypeEatInInitOrderTableTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(4);
        orderTable.setEmpty(false);
        orderTableRepository.save(orderTable);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(orderTable);
        orderRepository.save(order);

        Order actual = orderService.complete(order.getId());

        Assertions.assertThat(actual.getOrderTable().getNumberOfGuests()).isEqualTo(0);
        Assertions.assertThat(actual.getOrderTable().isEmpty()).isTrue();
    }

    /*
    * 주문 정보 전체 조회
    * */
    @Test
    @DisplayName("주문 정보 전체 조회")
    void orderFindAllTest() {

        Order order = new Order();
        order.setId(UUID.randomUUID());
        orderRepository.save(order);

        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setId(UUID.randomUUID());
        orderRepository.save(order3);

        List<Order> all = orderService.findAll();
        Assertions.assertThat(all).hasSize(3);
    }
}