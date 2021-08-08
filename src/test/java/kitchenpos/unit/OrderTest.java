package kitchenpos.unit;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderTest extends UnitTestRunner {

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

    @DisplayName("주문 생성 ( OrderType 'EAT_IN' )")
    @Test
    public void create_with_type_eat_in() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        when(menuRepository.findAllById(List.of(menuId))).thenReturn(List.of(stubbedMenu));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(stubbedMenu));

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final OrderTable stubbedOrderTable = new OrderTable();
        final UUID orderTableId = UUID.randomUUID();
        stubbedOrderTable.setName("테이블 1번");
        stubbedOrderTable.setId(orderTableId);
        stubbedOrderTable.setNumberOfGuests(0);
        stubbedOrderTable.setEmpty(true);

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(stubbedOrderTable));

        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(List.of(orderLineItem));
        request.setOrderTableId(orderTableId);

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.EAT_IN);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.WAITING);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder.setOrderTable(stubbedOrderTable);

        when(orderRepository.save(any(Order.class))).thenReturn(stubbedOrder);

        //when
        final Order createOrder = orderService.create(request);

        //then
        assertThat(createOrder.getId()).isNotNull();
        assertThat(createOrder.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(createOrder.getOrderDateTime()).isNotNull();
        assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(createOrder.getOrderLineItems().get(0)).isEqualTo(orderLineItem);
        assertThat(createOrder.getOrderTable()).isEqualTo(stubbedOrderTable);
    }

    @DisplayName("주문 생성 ( OrderType 'DELIVERY' )")
    @Test
    public void create_with_type_delivery() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        when(menuRepository.findAllById(List.of(menuId))).thenReturn(List.of(stubbedMenu));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(stubbedMenu));

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order request = new Order();
        final String deliveryAddress = "서울시 중랑구";
        request.setType(OrderType.DELIVERY);
        request.setDeliveryAddress(deliveryAddress);
        request.setOrderLineItems(List.of(orderLineItem));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.DELIVERY);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.WAITING);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder.setDeliveryAddress(deliveryAddress);

        when(orderRepository.save(any(Order.class))).thenReturn(stubbedOrder);

        //when
        final Order createOrder = orderService.create(request);

        //then
        assertThat(createOrder.getId()).isNotNull();
        assertThat(createOrder.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(createOrder.getOrderDateTime()).isNotNull();
        assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(createOrder.getOrderLineItems().get(0)).isEqualTo(orderLineItem);
    }

    @DisplayName("주문 수락 ( 'DELIVERY' )")
    @Test
    public void accept_type_delivery() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        final String deliveryAddress = "서울시 중랑구";
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.DELIVERY);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.WAITING);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.accept(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(kitchenridersClient, times(1))
                .requestDelivery(orderId, orderLineItem.getPrice(), deliveryAddress);

    }

    @DisplayName("주문 수락 ( 'TAKE_OUT' )")
    @Test
    public void accept_type_takeout() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.TAKEOUT);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.WAITING);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.accept(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

    }

    @DisplayName("주문 제공완료")
    @Test
    public void serve() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.TAKEOUT);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.ACCEPTED);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.serve(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 배달 시작")
    @Test
    public void startDelivery() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        final String deliveryAddress = "서울시 중랑구";
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.DELIVERY);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.SERVED);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.startDelivery(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);

    }

    @DisplayName("주문 배달 완료")
    @Test
    public void completeDelivery() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        final String deliveryAddress = "서울시 중랑구";
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.DELIVERY);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.DELIVERING);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.completeDelivery(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);

    }

    @DisplayName("주문 완료 ( 'TAKEOUT' )")
    @Test
    public void compete_with_type_takeout() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.TAKEOUT);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.SERVED);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.complete(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 완료 ( 'DELIVERY' )")
    @Test
    public void compete_with_type_delivery() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder = new Order();
        final UUID orderId = UUID.randomUUID();
        final String deliveryAddress = "서울시 중랑구";
        stubbedOrder.setId(orderId);
        stubbedOrder.setType(OrderType.DELIVERY);
        stubbedOrder.setOrderDateTime(LocalDateTime.now());
        stubbedOrder.setStatus(OrderStatus.DELIVERED);
        stubbedOrder.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(stubbedOrder));

        //when
        final Order acceptOrder = orderService.complete(orderId);

        //then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("모든 주문 조회")
    @Test
    public void findAll() {
        //given
        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setPrice(BigDecimal.valueOf(10000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(stubbedMenu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(10000));

        final Order stubbedOrder_1 = new Order();
        final String deliveryAddress = "서울시 중랑구";
        stubbedOrder_1.setId(UUID.randomUUID());
        stubbedOrder_1.setType(OrderType.DELIVERY);
        stubbedOrder_1.setOrderDateTime(LocalDateTime.now());
        stubbedOrder_1.setStatus(OrderStatus.DELIVERED);
        stubbedOrder_1.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder_1.setDeliveryAddress(deliveryAddress);

        final Order stubbedOrder_2 = new Order();
        stubbedOrder_2.setId(UUID.randomUUID());
        stubbedOrder_2.setType(OrderType.TAKEOUT);
        stubbedOrder_2.setOrderDateTime(LocalDateTime.now());
        stubbedOrder_2.setStatus(OrderStatus.SERVED);
        stubbedOrder_2.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder_2.setDeliveryAddress(deliveryAddress);

        final Order stubbedOrder_3 = new Order();
        stubbedOrder_3.setId(UUID.randomUUID());
        stubbedOrder_3.setType(OrderType.EAT_IN);
        stubbedOrder_3.setOrderDateTime(LocalDateTime.now());
        stubbedOrder_3.setStatus(OrderStatus.DELIVERED);
        stubbedOrder_3.setOrderLineItems(List.of(orderLineItem));
        stubbedOrder_3.setDeliveryAddress(deliveryAddress);

        when(orderRepository.findAll()).thenReturn(List.of(stubbedOrder_1, stubbedOrder_2, stubbedOrder_3));

        //when
        final List<Order> orders = orderService.findAll();

        //then
        assertThat(orders.size()).isEqualTo(3);
        assertThat(orders.get(0)).isEqualTo(stubbedOrder_1);
        assertThat(orders.get(1)).isEqualTo(stubbedOrder_2);
        assertThat(orders.get(2)).isEqualTo(stubbedOrder_3);

    }

}
