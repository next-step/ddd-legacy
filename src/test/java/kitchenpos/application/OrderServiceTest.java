package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  private OrderService orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private OrderTableRepository orderTableRepository;

  @Mock
  private KitchenridersClient kitchenridersClient;

  @BeforeEach
  void setUp() {
    this.orderService = new OrderService(
        orderRepository,
        menuRepository,
        orderTableRepository,
        kitchenridersClient
    );
  }

  @DisplayName("유효한 매장식사 주문정보를 입력하면 생성된 주문을 반환한다")
  @Test
  void givenValidEatInOrder_whenCreate_thenReturnOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setOccupied(true);

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
    given(orderRepository.save(any(Order.class))).willReturn(order);

    // when
    Order createdOrder = orderService.create(order);

    // then
    assertThat(createdOrder.getId()).isNotNull();
    assertThat(createdOrder.getType()).isEqualTo(OrderType.EAT_IN);
    assertThat(createdOrder.getOrderTableId()).isEqualTo(orderTable.getId());
    assertThat(createdOrder.getOrderLineItems()).hasSize(1);
    assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenuId)
        .contains(menu.getId());
  }

  @DisplayName("유효한 배달 주문정보를 입력하면 생성된 주문을 반환한다")
  @Test
  void givenValidDeliveryOrder_whenCreate_thenReturnOrder() {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setDisplayed(true);
    menu.setPrice(BigDecimal.valueOf(23000));

    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(BigDecimal.valueOf(23000));
    orderLineItem.setQuantity(3);

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(OrderType.DELIVERY);
    order.setOrderLineItems(List.of(orderLineItem));
    order.setDeliveryAddress("서울시 강남구");

    given(menuRepository.findAllByIdIn(anyList())).willReturn(List.of(menu));
    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));
    given(orderRepository.save(any(Order.class))).willReturn(order);

    // when
    Order createdOrder = orderService.create(order);

    // then
    assertThat(createdOrder.getId()).isNotNull();
    assertThat(createdOrder.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(createdOrder.getDeliveryAddress()).isEqualTo("서울시 강남구");
    assertThat(createdOrder.getOrderLineItems()).hasSize(1);
    assertThat(createdOrder.getOrderLineItems()).extracting(OrderLineItem::getMenuId)
        .contains(menu.getId());
  }

}
