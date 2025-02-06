package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final KitchenridersClient kitchenridersClient = mock(KitchenridersClient.class);
    private final OrderService orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

    @DisplayName("매장 주문을 등록할 수 있다.")
    @Test
    void createEatInOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        orderTable.setOccupied(true);

        Order eatInOrder = makeTestEatInOrder(OrderStatus.WAITING, orderLineItem, orderTable);

        when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any(Order.class))).thenReturn(eatInOrder);

        // when
        Order resultOrder = orderService.create(eatInOrder);

        // then
        assertThat(resultOrder.getId()).isNotNull();
        assertThat(resultOrder.getType()).isEqualTo(eatInOrder.getType());
        assertThat(resultOrder.getStatus()).isEqualTo(eatInOrder.getStatus());
        assertThat(resultOrder.getOrderDateTime()).isEqualTo(eatInOrder.getOrderDateTime());
        assertThat(resultOrder.getOrderLineItems()).isEqualTo(eatInOrder.getOrderLineItems());
        assertThat(resultOrder.getOrderTable()).isEqualTo(eatInOrder.getOrderTable());
    }

    @DisplayName("테이크 아웃 주문을 등록할 수 있다.")
    @Test
    void createTakeOutOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order takeOutOrder = makeTestTakeOutOrder(OrderStatus.WAITING, orderLineItem);

        when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any(Order.class))).thenReturn(takeOutOrder);

        // when
        Order resultOrder = orderService.create(takeOutOrder);

        // then
        assertThat(resultOrder.getId()).isNotNull();
        assertThat(resultOrder.getType()).isEqualTo(takeOutOrder.getType());
        assertThat(resultOrder.getStatus()).isEqualTo(takeOutOrder.getStatus());
        assertThat(resultOrder.getOrderDateTime()).isEqualTo(takeOutOrder.getOrderDateTime());
        assertThat(resultOrder.getOrderLineItems()).isEqualTo(takeOutOrder.getOrderLineItems());
    }

    @DisplayName("배달 주문을 등록할 수 있다.")
    @Test
    void createDeliveryOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order deliveryOrder = makeTestDeliveryOrder(OrderStatus.WAITING, orderLineItem, "집");

        when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any(Order.class))).thenReturn(deliveryOrder);

        // when
        Order resultOrder = orderService.create(deliveryOrder);

        // then
        assertThat(resultOrder.getId()).isNotNull();
        assertThat(resultOrder.getType()).isEqualTo(deliveryOrder.getType());
        assertThat(resultOrder.getStatus()).isEqualTo(deliveryOrder.getStatus());
        assertThat(resultOrder.getOrderDateTime()).isEqualTo(deliveryOrder.getOrderDateTime());
        assertThat(resultOrder.getOrderLineItems()).isEqualTo(deliveryOrder.getOrderLineItems());
        assertThat(resultOrder.getDeliveryAddress()).isEqualTo(deliveryOrder.getDeliveryAddress());
    }

    @DisplayName("매장 주문을 수락할 수 있다.")
    @Test
    void acceptEatInOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        orderTable.setOccupied(true);

        Order eatInOrder = makeTestEatInOrder(OrderStatus.WAITING, orderLineItem, orderTable);

        when(orderRepository.findById(any())).thenReturn(Optional.of(eatInOrder));

        // when
        Order resultOrder = orderService.accept(eatInOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("테이크 아웃 주문을 수락할 수 있다.")
    @Test
    void acceptTakeOutOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order eatInOrder = makeTestTakeOutOrder(OrderStatus.WAITING, orderLineItem);

        when(orderRepository.findById(any())).thenReturn(Optional.of(eatInOrder));

        // when
        Order resultOrder = orderService.accept(eatInOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("배달 주문을 수락할 수 있다.")
    @Test
    void acceptDeliveryOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order deliveryOrder = makeTestDeliveryOrder(OrderStatus.WAITING, orderLineItem, "집");

        when(orderRepository.findById(any())).thenReturn(Optional.of(deliveryOrder));

        // when
        Order resultOrder = orderService.accept(deliveryOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("매장 주문이 들어왔다면 메뉴를 준비한다.")
    @Test
    void serveEatInOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        orderTable.setOccupied(true);

        Order eatInOrder = makeTestEatInOrder(OrderStatus.ACCEPTED, orderLineItem, orderTable);

        when(orderRepository.findById(any())).thenReturn(Optional.of(eatInOrder));

        // when
        Order resultOrder = orderService.serve(eatInOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("테이크 아웃 주문이 들어왔다면 메뉴를 준비한다.")
    @Test
    void serveTakeOutOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order eatInOrder = makeTestTakeOutOrder(OrderStatus.ACCEPTED, orderLineItem);

        when(orderRepository.findById(any())).thenReturn(Optional.of(eatInOrder));

        // when
        Order resultOrder = orderService.serve(eatInOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 주문이 들어왔다면 메뉴를 준비한다.")
    @Test
    void serveDeliveryOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order deliveryOrder = makeTestDeliveryOrder(OrderStatus.ACCEPTED, orderLineItem, "집");

        when(orderRepository.findById(any())).thenReturn(Optional.of(deliveryOrder));

        // when
        Order resultOrder = orderService.serve(deliveryOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달을 시작한다.")
    @Test
    void startDelivery() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order deliveryOrder = makeTestDeliveryOrder(OrderStatus.SERVED, orderLineItem, "집");

        when(orderRepository.findById(any())).thenReturn(Optional.of(deliveryOrder));

        // when
        Order resultOrder = orderService.startDelivery(deliveryOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달이 완료된다.")
    @Test
    void completeDelivery() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order deliveryOrder = makeTestDeliveryOrder(OrderStatus.DELIVERING, orderLineItem, "집");

        when(orderRepository.findById(any())).thenReturn(Optional.of(deliveryOrder));

        // when
        Order resultOrder = orderService.completeDelivery(deliveryOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("매장 주문을 완료한다.")
    @Test
    void completeEatInOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        orderTable.setOccupied(true);

        Order eatInOrder = makeTestEatInOrder(OrderStatus.SERVED, orderLineItem, orderTable);

        when(orderRepository.findById(any())).thenReturn(Optional.of(eatInOrder));
        when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class))).thenReturn(false);

        // when
        Order resultOrder = orderService.complete(eatInOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @DisplayName("테이크 아웃 주문을 완료한다.")
    @Test
    void completeTakeOutOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order eatInOrder = makeTestTakeOutOrder(OrderStatus.SERVED, orderLineItem);

        when(orderRepository.findById(any())).thenReturn(Optional.of(eatInOrder));

        // when
        Order resultOrder = orderService.complete(eatInOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("배달 주문을 완료된다.")
    @Test
    void completeDeliveryOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order deliveryOrder = makeTestDeliveryOrder(OrderStatus.DELIVERED, orderLineItem, "집");

        when(orderRepository.findById(any())).thenReturn(Optional.of(deliveryOrder));

        // when
        Order resultOrder = orderService.complete(deliveryOrder.getId());

        // then
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}