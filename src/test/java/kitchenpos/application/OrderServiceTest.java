package kitchenpos.application;

import kitchenpos.DummyData;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest extends DummyData {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("매장&포장 주문 생성")
    @Test
    void createOrder() {
        Order order = orders.get(0);
        OrderTable orderTable = order.getOrderTable();
        orderTable.setEmpty(TABLE_SIT);
        List<OrderLineItem> orderLineItem = order.getOrderLineItems();
        Menu menu = orderLineItem.get(0).getMenu();

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.save(any())).willReturn(order);

        Order createOrder = orderService.create(order);

        assertAll(
                () -> assertThat(createOrder).isNotNull(),
                () -> assertThat(createOrder.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("배송 주문 생성")
    @Test
    void createDeliveryOrder() {
        Order deliveryOrder = orders.get(1);
        List<OrderLineItem> orderLineItem = deliveryOrder.getOrderLineItems();
        Menu menu = orderLineItem.get(0).getMenu();

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderRepository.save(any())).willReturn(deliveryOrder);

        Order createOrder = orderService.create(deliveryOrder);

        assertAll(
                () -> assertThat(createOrder).isNotNull(),
                () -> assertThat(createOrder.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("주문 타입 없으면 예외처리")
    @Test
    void negativeOrderType() {
        Order order = orders.get(0);
        order.setType(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 아이템 없으면 예외처리")
    @Test
    void negativeOrderLineItem() {
        Order order = orders.get(0);
        order.setOrderLineItems(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("배송, 포장 주문은 아이템 수량 0 미만이면 예외처리")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void negativeDeliveryAndTakeOutQuantity(OrderType type) {
        Order order = orders.get(0);
        order.setType(type);
        OrderLineItem orderLineItem = order.getOrderLineItems().get(0);
        orderLineItem.setQuantity(-1);

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(orderLineItem.getMenu()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 메뉴 비공개면 에러 처리")
    @Test
    void negativeMenuDisplay() {
        Order order = orders.get(0);
        OrderLineItem orderLineItem = order.getOrderLineItems().get(0);
        Menu menu = orderLineItem.getMenu();
        menu.setDisplayed(MENU_HIDE);

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(orderLineItem.getMenu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 아이템 가격이 메뉴보다 비싸면 에러 처리")
    @Test
    void orderLineItemPriceHigherThenmenuPrice() {
        Order order = orders.get(0);
        OrderLineItem orderLineItem = order.getOrderLineItems().get(0);
        orderLineItem.setPrice(ofPrice(100000));
        Menu menu = orderLineItem.getMenu();

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(orderLineItem.getMenu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("배송 주문은 배송지 없으면 에러")
    @Test
    void negativeDeliveryAddress() {
        Order order = orders.get(1);
        List<OrderLineItem> orderLineItem = order.getOrderLineItems();
        Menu menu = orderLineItem.get(0).getMenu();

        order.setDeliveryAddress(null);

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("테이블이 비어있으면 예외처리")
    @Test
    void negativeOrderTableEmpty() {
        Order order = orders.get(0);
        OrderTable orderTable = order.getOrderTable();
        orderTable.setEmpty(TABLE_CLEAR);
        List<OrderLineItem> orderLineItem = order.getOrderLineItems();
        Menu menu = orderLineItem.get(0).getMenu();

        given(menuRepository.findAllById(any())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("메뉴 없으면 예외처리")
    @Test
    void negativeMenu() {
        Order order = orders.get(0);
        List<OrderLineItem> orderLineItem = order.getOrderLineItems();
        orderLineItem.get(0).setMenu(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 접수")
    @Test
    void orderAccept() {
        Order order = orders.get(0);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order accept = orderService.accept(order.getId());

        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("배송 주문 접수하면 배달라이더에게 주문 정보 전달 ")
    @Test
    void orderAcceptKitchenriders() {
        Order order = orders.get(1);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order accept = orderService.accept(order.getId());

        verify(kitchenridersClient, times(1)).requestDelivery(any(), any(), any());
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 접수는 대기상태가 아니면 불가능")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void negativeAccept(OrderStatus status) {
        Order order = orders.get(0);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("주문 제공")
    @Test
    void serve() {
        Order order = orders.get(0);
        order.setStatus(OrderStatus.ACCEPTED);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order serve = orderService.serve(order.getId());

        assertThat(serve.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 제공은 접수상태가 아니면 불가능")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void negativeServe(OrderStatus status) {
        Order order = orders.get(0);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("배송 시작")
    @Test
    void startDelivery() {
        Order order = orders.get(1);
        order.setStatus(OrderStatus.SERVED);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order startDelivery = orderService.startDelivery(order.getId());

        assertThat(startDelivery.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배송 주문이 아니면 배송 불가")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void negativeStartDeliveryOrderType(OrderType type) {
        Order order = orders.get(1);
        order.setType(type);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("주문 제공이 아니면 배송 불가")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void negativeStartDeliveryServe(OrderStatus status) {
        Order order = orders.get(1);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("배송 완료")
    @Test
    void completeDelivery() {
        Order order = orders.get(1);
        order.setStatus(OrderStatus.DELIVERING);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order completeDelivery = orderService.completeDelivery(order.getId());

        assertThat(completeDelivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배송 진행이 아니면 배송완료 불가")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    void negativeCompleteDelivery(OrderStatus status) {
        Order order = orders.get(1);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("배달 주문 완료")
    @Test
    void deliveryComplete() {
        Order order = orders.get(1);
        order.setStatus(OrderStatus.DELIVERED);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order complete = orderService.complete(order.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("배송이 안되면 주문완료 불가")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "SERVED", "COMPLETED"})
    void negativeDeliveryComplete(OrderStatus status) {
        Order order = orders.get(1);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("포장&매장 주문 완료")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
    void takeOutAndEatInComplete(OrderType type) {
        Order order = orders.get(0);
        order.setType(type);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        Order complete = orderService.complete(order.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장주문은 제공안하면 주문 완료 불가")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void negativeTakeOutComplete(OrderStatus status) {
        Order order = orders.get(0);
        order.setType(OrderType.TAKEOUT);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("매장주문은 제공안하면 주문 완료 불가")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void negativeEatInComplete(OrderStatus status) {
        Order order = orders.get(0);
        order.setStatus(status);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("주문 내역 조회")
    @Test
    void findAll() {
        given(orderRepository.findAll()).willReturn(orders);

        List<Order> findAll = orderRepository.findAll();

        verify(orderRepository).findAll();
        verify(orderRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(orders.containsAll(findAll)).isTrue(),
                () -> assertThat(orders.size()).isEqualTo(findAll.size())
        );
    }
}