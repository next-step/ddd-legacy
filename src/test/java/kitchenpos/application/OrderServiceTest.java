package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문 테이블")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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

  private OrderTable 테이블_1번;
  private Menu menu;

  private Order orderEatIn;
  private Order orderTakeOut;
  private Order orderDelivery;

  @BeforeEach
  void setUp() {
    테이블_1번 = OrderTableFixture.createOrderTable();

    menu = MenuFixture.createMenu();

    orderEatIn = OrderFixture.createOrderEatIn();

    orderTakeOut = OrderFixture.createOrderTakeOut();

    orderDelivery = OrderFixture.createOrderDelivery();
  }

  @DisplayName("주문 매장식사 등록")
  @Test
  void createOrderEatIn() {
    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(테이블_1번));
    when(orderRepository.save(any())).thenReturn(orderEatIn);

    Order result = orderService.create(orderEatIn);

    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
  }

  @DisplayName("주문 포장 등록")
  @Test
  void createOrderTakeOut() {
    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
    when(orderRepository.save(any())).thenReturn(orderTakeOut);

    Order result = orderService.create(orderTakeOut);

    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
  }

  @DisplayName("주문 배달 등록")
  @Test
  void createOrderDelivery() {
    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
    when(orderRepository.save(any())).thenReturn(orderTakeOut);

    Order result = orderService.create(orderTakeOut);

    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
  }

  @DisplayName("주문 타입 null 에러")
  @Test
  void orderTypeNull() {
    orderTakeOut.setType(null);

    assertThatThrownBy(() -> orderService.create(orderTakeOut)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("주문 아이템 null 에러")
  @Test
  void orderLineItemNull() {
    orderTakeOut.setOrderLineItems(null);

    assertThatThrownBy(() -> orderService.create(orderTakeOut)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("노출되지 않은 메뉴는 선택하면 에러")
  @Test
  void menuDisplayHideSelect() {
    when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    menu.setDisplayed(false);

    assertThatThrownBy(() -> orderService.create(orderTakeOut)).isInstanceOf(IllegalStateException.class);
  }

  @DisplayName("주문 배달 수락")
  @Test
  void orderDeliveryAccept() {
    when(orderRepository.findById(any())).thenReturn(Optional.of(orderDelivery));

    Order result = orderService.accept(orderDelivery.getId());

    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
  }

  @DisplayName("주문 매장식사 수락")
  @Test
  void orderEatInAccept() {
    when(orderRepository.findById(any())).thenReturn(Optional.of(orderEatIn));

    Order result = orderService.accept(orderEatIn.getId());

    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
  }

  @DisplayName("주문 포장 수락")
  @Test
  void orderTakeOutAccept() {
    when(orderRepository.findById(any())).thenReturn(Optional.of(orderTakeOut));

    Order result = orderService.accept(orderTakeOut.getId());

    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
  }

  @DisplayName("주문 배달 서빙")
  @Test
  void orderDeliveryServe() {
    orderDelivery.setStatus(OrderStatus.ACCEPTED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderDelivery));

    Order result = orderService.serve(orderDelivery.getId());

    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
  }

  @DisplayName("주문 매장식사 서빙")
  @Test
  void orderEatInServe() {
    orderEatIn.setStatus(OrderStatus.ACCEPTED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderEatIn));

    Order result = orderService.serve(orderEatIn.getId());

    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
  }

  @DisplayName("주문 포장 서빙")
  @Test
  void orderTakeOutServe() {
    orderTakeOut.setStatus(OrderStatus.ACCEPTED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderTakeOut));

    Order result = orderService.serve(orderTakeOut.getId());

    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
  }

  @DisplayName("주문 배달 배달 시작")
  @Test
  void orderDeliveryStart() {
    orderDelivery.setStatus(OrderStatus.SERVED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderDelivery));

    Order result = orderService.startDelivery(orderDelivery.getId());

    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
  }

  @DisplayName("주문 배달 배달 완료")
  @Test
  void orderDeliveryComplete() {
    orderDelivery.setStatus(OrderStatus.DELIVERING);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderDelivery));

    Order result = orderService.completeDelivery(orderDelivery.getId());

    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
  }

  @DisplayName("주문 완료(배달)")
  @Test
  void orderCompleteDelivery() {
    orderDelivery.setStatus(OrderStatus.DELIVERED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderDelivery));

    Order result = orderService.complete(orderDelivery.getId());

    assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
  }

  @DisplayName("주문 완료(매장식사)")
  @Test
  void orderCompleteEatIn() {
    orderEatIn.setStatus(OrderStatus.SERVED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderEatIn));

    Order result = orderService.complete(orderEatIn.getId());

    assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
  }

  @DisplayName("주문 완료(포장)")
  @Test
  void orderCompleteTakeOut() {
    orderTakeOut.setStatus(OrderStatus.SERVED);

    when(orderRepository.findById(any())).thenReturn(Optional.of(orderTakeOut));

    Order result = orderService.complete(orderEatIn.getId());

    assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
  }
}