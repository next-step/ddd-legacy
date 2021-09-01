package kitchenpos.application;

import kitchenpos.commons.*;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderGenerator orderGenerator = new OrderGenerator();
    private OrderTableGenerator orderTableGenerator = new OrderTableGenerator();
    private OrderLineItemGenerator orderLineItemGenerator = new OrderLineItemGenerator();
    private MenuGenerator menuGenerator = new MenuGenerator();
    private MenuGroupGenerator menuGroupGenerator = new MenuGroupGenerator();
    private ProductGenerator productGenerator = new ProductGenerator();
    private MenuProductGenerator menuProductGenerator = new MenuProductGenerator();

    private OrderTable mockOrderTable;
    private Menu mockMenu;
    private OrderLineItem mockOrderLineItem;
    private List<OrderLineItem> mockOrderLineItems;
    private Order mockOrder;

    @Test
    @DisplayName("주문 추가 Eat In - 성공")
    void createOrder_EatIn() {
        // given
        generateOrderRequest(OrderType.EAT_IN);

        // mocking
        given(orderRepository.save(any())).willReturn(mockOrder);
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(mockOrderTable));

        // when
        Order newOrder = orderService.create(mockOrder);

        // then
        assertThat(newOrder.getId()).isNotNull();
    }

    @Test
    @DisplayName("주문 추가 Takeout - 성공")
    void createOrder_Takeout() {
        // given
        generateOrderRequest(OrderType.TAKEOUT);

        // mocking
        given(orderRepository.save(any())).willReturn(mockOrder);
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when
        Order newOrder = orderService.create(mockOrder);

        // then
        assertThat(newOrder.getId()).isNotNull();
    }

    @Test
    @DisplayName("주문 추가 Delivery - 성공")
    void createOrder_Delivery() {
        // given
        generateOrderRequest(OrderType.DELIVERY);

        // mocking
        given(orderRepository.save(any())).willReturn(mockOrder);
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when
        Order newOrder = orderService.create(mockOrder);

        // then
        assertThat(newOrder.getId()).isNotNull();
    }

    private void generateOrderRequest(OrderType orderType) {
        MenuGroup mockMenuGroup = menuGroupGenerator.generateRequest();
        Product mockProduct = productGenerator.generateRequest();
        MenuProduct mockMenuProduct = menuProductGenerator.generateRequestByProduct(mockProduct);
        mockMenu = menuGenerator.generateRequestByMenuGroupAndMenuProducts(mockMenuGroup, Collections.singletonList(mockMenuProduct));

        mockOrderTable = orderTableGenerator.generateRequest();

        mockOrderLineItem = orderLineItemGenerator.generateRequestByMenu(mockMenu);
        mockOrderLineItems = new ArrayList<>();
        mockOrderLineItems.add(mockOrderLineItem);

        mockOrder = orderGenerator.generateRequestByOrderTypeAndOrderTableAndOrderLineItems(orderType, mockOrderTable, mockOrderLineItems);
    }

    @Test
    @DisplayName("주문 추가 - 실패: 없는 OrderType 값")
    void addOrder_IllegalArgumentException_Empty_OrderType() {
        // given
        generateOrderRequest(null);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 없거나 빈 OrderLineItems")
    void addOrder_IllegalArgumentException_NullOrEmpty_OrderLineItems() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setOrderLineItems(null);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 주문 단위 개수와 메뉴개수가 다름")
    void addOrder_IllegalArgumentException_Diff_menuSizeAndOrderLineItemsSize() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrderLineItems.add(mockOrderLineItem);

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 매장식사가 아닐때 1개 미만의 주문 수량 입력")
    void addOrder_IllegalArgumentException_Invalid_Quantity() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrderLineItem.setQuantity(-1);

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 판매하지 않는 메뉴 선택")
    void addOrder_IllegalArgumentException_HideMenu() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockMenu.setDisplayed(false);

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 주문 단위의 가격과 메뉴의 가격이 다름")
    void addOrder_IllegalArgumentException_Diff_MenuPriceAndOrderLineItemPrice() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockMenu.setPrice(BigDecimal.valueOf(999));

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 배달 주문의 배달주소가 없음")
    void addOrder_IllegalArgumentException_Empty_DeliveryAddress() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrder.setDeliveryAddress(null);

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 매장 식사 주문의 주문 테이블 id가  유효하지 않음")
    void addOrder_NoSuchElementException_Invalid_OrderTableId() {
        // given
        generateOrderRequest(OrderType.EAT_IN);

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("주문 추가 - 실패: 매장 식사 주문의 주문 테이블에 손님이 없음")
    void addOrder_IllegalStateException_OrderTable_isEmpty() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrderTable.setEmpty(true);

        // mocking
        given(menuRepository.findAllById(any())).willReturn(Collections.singletonList(mockMenu));
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(mockOrderTable));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.create(mockOrder));
    }

    @Test
    @DisplayName("Order Accept Eat In - 성공")
    void acceptOrder() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.WAITING);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.accept(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Order Accept Takeout - 성공")
    void acceptOrder_Takeout() {
        // given
        generateOrderRequest(OrderType.TAKEOUT);
        mockOrder.setStatus(OrderStatus.WAITING);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.accept(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Order Accept Delivery - 성공")
    void acceptOrder_Delivery() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrder.setStatus(OrderStatus.WAITING);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));
        willDoNothing().given(kitchenridersClient).requestDelivery(any(), any(), any());

        // when
        Order accept = orderService.accept(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Order Accept - 실패: 유효하지 않는 OrderId")
    void acceptOrder_NoSuchElementException_Invalid_OrderId() {
        // given
        generateOrderRequest(OrderType.EAT_IN);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.accept(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order Accept - 실패: 상태가 대기중이 아니다")
    void acceptOrder_IllegalStateException_Status_Not_Waiting() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.COMPLETED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.accept(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order Serve - 성공")
    void serveOrder() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.ACCEPTED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.serve(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("Order Serve - 실패: 유효하지 않는 OrderId")
    void serveOrder_NoSuchElementException_Invalid_OrderId() {
        // given
        generateOrderRequest(OrderType.EAT_IN);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.serve(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order Serve - 실패: 상태가 Accept 가 아니다")
    void serveOrder_IllegalStateException_Status_Not_Waiting() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.COMPLETED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.serve(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order StartDelivery - 성공")
    void startDeliveryOrder() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrder.setStatus(OrderStatus.SERVED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.startDelivery(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("Order StartDelivery - 실패: 유효하지 않는 OrderId")
    void startDeliveryOrder_NoSuchElementException_Invalid_OrderId() {
        // given
        generateOrderRequest(OrderType.EAT_IN);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.startDelivery(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order StartDelivery - 실패: OrderType 이 Delivery 가 아니다")
    void startDeliveryOrder_IllegalStateException_OrderType_Not_Delivery() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.SERVED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.startDelivery(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order StartDelivery - 실패: 상태가 Served 가 아니다")
    void startDeliveryOrder_IllegalStateException_Status_Not_Served() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.COMPLETED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.startDelivery(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order CompleteDelivery - 성공")
    void completeDeliveryOrder() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrder.setStatus(OrderStatus.DELIVERING);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.completeDelivery(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("Order CompleteDelivery - 실패: 유효하지 않는 OrderId")
    void completeDeliveryOrder_NoSuchElementException_Invalid_OrderId() {
        // given
        generateOrderRequest(OrderType.DELIVERY);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.completeDelivery(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order CompleteDelivery - 실패: OrderType 이 Delivery 가 아니다")
    void completeDeliveryOrder_IllegalStateException_OrderType_Not_Delivery() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.DELIVERING);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.completeDelivery(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order CompleteDelivery - 실패: 상태가 Delivering 가 아니다")
    void completeDeliveryOrder_IllegalStateException_Status_Not_Delivering() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrder.setStatus(OrderStatus.COMPLETED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.completeDelivery(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order Complete Eat-in - 성공")
    void completeOrder_EatIn() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.SERVED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.complete(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Order Complete Takeout - 성공")
    void completeOrder_Takeout() {
        // given
        generateOrderRequest(OrderType.TAKEOUT);
        mockOrder.setStatus(OrderStatus.SERVED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.complete(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Order Complete Delivery - 성공")
    void completeOrder_Delivery() {
        // given
        generateOrderRequest(OrderType.DELIVERY);
        mockOrder.setStatus(OrderStatus.SERVED);
        mockOrder.setStatus(OrderStatus.DELIVERED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when
        Order accept = orderService.complete(mockOrder.getId());

        // then
        assertThat(accept).isNotNull();
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Order Complete - 실패: 유효하지 않는 OrderId")
    void completeOrder_NoSuchElementException_Invalid_OrderId() {
        // given
        generateOrderRequest(OrderType.DELIVERY);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.complete(mockOrder.getId()));
    }

    @ParameterizedTest
    @DisplayName("Order Complete - 실패: 현재 완료할 수 없는 상태다")
    @EnumSource(OrderType.class)
    void completeOrder_IllegalStateException_Cannot_Transfer_Completed_Status(OrderType orderType) {
        // given
        generateOrderRequest(orderType);
        mockOrder.setStatus(OrderStatus.COMPLETED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.complete(mockOrder.getId()));
    }

    @Test
    @DisplayName("Order Complete - 성공: 매장 식사일 때 주문 테이블이 주문이 모두 완료되면 주문테이블을 비운다")
    void completeOrder_NoSuchElementException_() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        mockOrder.setStatus(OrderStatus.SERVED);

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(mockOrder));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

        // when
        Order completeOrder = orderService.complete(mockOrder.getId());

        // then
        OrderTable orderTableOfCompleteOrder = completeOrder.getOrderTable();
        assertThat(orderTableOfCompleteOrder.getNumberOfGuests()).isEqualTo(0);
        assertThat(orderTableOfCompleteOrder.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("모든 주문 조회")
    void findAllOrder() {
        // given
        generateOrderRequest(OrderType.EAT_IN);
        int size = 10;
        List<Order> mockOrders = generateOrders(size);

        // mocking
        given(orderRepository.findAll()).willReturn(mockOrders);

        // when
        List<Order> Orders = orderService.findAll();

        // then
        Order order = Orders.get(0);
        assertAll(
                () -> assertThat(order.getId()).isEqualTo(mockOrder.getId()),
                () -> assertThat(order.getOrderDateTime()).isEqualTo(mockOrder.getOrderDateTime()),
                () -> assertThat(order.getOrderTable()).isEqualTo(mockOrder.getOrderTable()),
                () -> assertThat(Orders.size()).isEqualTo(size)
        );

    }

    private List<Order> generateOrders(int size) {
        return IntStream.range(0, size).mapToObj(i -> mockOrder).collect(Collectors.toList());
    }

}