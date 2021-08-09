package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    OrderTableRepository orderTableRepository;

    @Autowired
    ProductRepository productRepository;

    private List<OrderLineItem> orderLineItems;

    private OrderTable emptyOrderTable;

    private OrderTable notEmptyOrderTable;

    private Menu hideMenu;

    private Order orderStatusWaiting;

    private Order orderStatusAccepted;

    private Order orderStatusServed;

    private Order orderStatusDelivering;

    private Order orderStatusDelivered;

    private Order eatInOrderStatusCompleted;

    private Order deliveryOrderStatusCompleted;

    @BeforeEach
    void setUp() {
        orderLineItems = new ArrayList<>();
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b"));
        orderLineItem.setPrice(BigDecimal.valueOf(16000));
        orderLineItem.setQuantity(3);
        orderLineItems.add(orderLineItem);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("100번");
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(0);
        emptyOrderTable = orderTableRepository.save(orderTable);

        final OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(UUID.randomUUID());
        orderTable2.setName("101번");
        orderTable2.setEmpty(true);
        orderTable2.setNumberOfGuests(0);
        notEmptyOrderTable = orderTableRepository.save(orderTable2);

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("추천 메뉴");
        MenuGroup saveMenuGroup = menuGroupRepository.save(menuGroup);

        final Product product = productRepository.findById(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"))
                .orElseThrow(NoSuchElementException::new);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);

        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("치킨");
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setDisplayed(false);
        menu.setMenuGroup(saveMenuGroup);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        hideMenu = menuRepository.save(menu);

        menu.setDisplayed(true);
        Menu displyedMenu = menuRepository.save(menu);

        final OrderLineItem orderLineItemTest = new OrderLineItem();
        orderLineItemTest.setMenu(displyedMenu);
        orderLineItemTest.setQuantity(1);

        final Order dummyOrderWaiting = new Order();
        dummyOrderWaiting.setId(UUID.randomUUID());
        dummyOrderWaiting.setType(OrderType.EAT_IN);
        dummyOrderWaiting.setStatus(OrderStatus.WAITING);
        dummyOrderWaiting.setOrderDateTime(LocalDateTime.now());
        dummyOrderWaiting.setOrderLineItems(Arrays.asList(orderLineItemTest));
        orderStatusWaiting = orderRepository.save(dummyOrderWaiting);

        final Order dummyOrderAccepted = new Order();
        dummyOrderAccepted.setId(UUID.randomUUID());
        dummyOrderAccepted.setType(OrderType.EAT_IN);
        dummyOrderAccepted.setStatus(OrderStatus.ACCEPTED);
        dummyOrderAccepted.setOrderDateTime(LocalDateTime.now());
        dummyOrderAccepted.setOrderLineItems(Arrays.asList(orderLineItemTest));
        orderStatusAccepted = orderRepository.save(dummyOrderAccepted);


        final Order dummyOrderServed = new Order();
        dummyOrderServed.setId(UUID.randomUUID());
        dummyOrderServed.setType(OrderType.DELIVERY);
        dummyOrderServed.setStatus(OrderStatus.SERVED);
        dummyOrderServed.setOrderDateTime(LocalDateTime.now());
        dummyOrderServed.setOrderLineItems(Arrays.asList(orderLineItemTest));
        dummyOrderServed.setDeliveryAddress("제주자치도 첨단로 1");
        orderStatusServed = orderRepository.save(dummyOrderServed);

        final Order dummyOrderDelivering = new Order();
        dummyOrderDelivering.setId(UUID.randomUUID());
        dummyOrderDelivering.setType(OrderType.DELIVERY);
        dummyOrderDelivering.setStatus(OrderStatus.DELIVERING);
        dummyOrderDelivering.setOrderDateTime(LocalDateTime.now());
        dummyOrderDelivering.setOrderLineItems(Arrays.asList(orderLineItemTest));
        dummyOrderServed.setDeliveryAddress("제주자치도 첨단로 1");
        orderStatusDelivering = orderRepository.save(dummyOrderDelivering);

        final Order dummyOrderDelivered = new Order();
        dummyOrderDelivered.setId(UUID.randomUUID());
        dummyOrderDelivered.setType(OrderType.DELIVERY);
        dummyOrderDelivered.setStatus(OrderStatus.DELIVERED);
        dummyOrderDelivered.setOrderDateTime(LocalDateTime.now());
        dummyOrderDelivered.setOrderLineItems(Arrays.asList(orderLineItemTest));
        dummyOrderServed.setDeliveryAddress("제주자치도 첨단로 1");
        orderStatusDelivered = orderRepository.save(dummyOrderDelivered);

        final Order dummyOrderCompleted = new Order();
        dummyOrderCompleted.setId(UUID.randomUUID());
        dummyOrderCompleted.setType(OrderType.EAT_IN);
        dummyOrderCompleted.setStatus(OrderStatus.SERVED);
        dummyOrderCompleted.setOrderDateTime(LocalDateTime.now());
        dummyOrderCompleted.setOrderLineItems(Arrays.asList(orderLineItemTest));
        eatInOrderStatusCompleted = orderRepository.save(dummyOrderCompleted);

        final Order dummyDeliveryOrderStatusCompleted = new Order();
        dummyDeliveryOrderStatusCompleted.setId(UUID.randomUUID());
        dummyDeliveryOrderStatusCompleted.setType(OrderType.DELIVERY);
        dummyDeliveryOrderStatusCompleted.setStatus(OrderStatus.COMPLETED);
        dummyDeliveryOrderStatusCompleted.setOrderDateTime(LocalDateTime.now());
        dummyDeliveryOrderStatusCompleted.setOrderLineItems(Arrays.asList(orderLineItemTest));
        deliveryOrderStatusCompleted = orderRepository.save(dummyDeliveryOrderStatusCompleted);
    }

    @DisplayName("주문 등록 성공")
    @Test
    void createOrderSuccess() {
        // Given
        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(emptyOrderTable.getId());
        request.setOrderLineItems(orderLineItems);

        // When
        final Order result = orderService.create(request);

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
    }

    @DisplayName("주문 등록 실패 - 타입이 없는 경우")
    @Test
    void createOrderFailTypeIsNull() {
        // Given
        final Order request = new Order();
        request.setOrderTableId(emptyOrderTable.getId());
        request.setOrderLineItems(orderLineItems);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 주문 목록이 없는 경우")
    @Test
    void createOrderFailOrderLineItemsIsNull() {
        // Given
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035520");

        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(emptyOrderTable.getId());

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 등록되지 않은 메뉴를 주문한 경우")
    @Test
    void createOrderFailNonExistentMenu() {
        // Given
        final UUID orderTableId = UUID.fromString("8d710043-29b6-420e-8452-233f5a035520");
        final OrderLineItem noneExistMenu = new OrderLineItem();
        noneExistMenu.setMenuId(UUID.randomUUID());
        orderLineItems.add(noneExistMenu);

        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(orderTableId);
        request.setOrderLineItems(orderLineItems);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 매장식사가 아닌데 주문목록의 수량이 음수인 경우")
    @Test
    void createOrderFailOrderLineItemMinusQuantity() {
        // Given
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.fromString("e1254913-8608-46aa-b23a-a07c1dcbc648"));
        orderLineItem.setPrice(BigDecimal.valueOf(16000));
        orderLineItem.setQuantity(-1);
        orderLineItems.add(orderLineItem);

        final Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderTableId(notEmptyOrderTable.getId());
        request.setOrderLineItems(orderLineItems);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 매장식사가 아닌데 주문목록의 수량이 음수인 경우")
    @Test
    void createOrderFailOrderLineItemHideMenu() {
        // Given
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(hideMenu.getId());
        orderLineItem.setPrice(BigDecimal.valueOf(16000));
        orderLineItem.setQuantity(1);
        orderLineItems.add(orderLineItem);

        final Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderTableId(notEmptyOrderTable.getId());
        request.setOrderLineItems(orderLineItems);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 배달주문일 때 배달 주소가 없는 경우")
    @Test
    void createOrderFailDeliveryOrderNullOrEmptyAddress() {
        // Given
        final Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderTableId(emptyOrderTable.getId());
        request.setOrderLineItems(orderLineItems);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 상태 변경 대기 -> 수락 성공")
    @Test
    void changeOrderAcceptSuccess() {
        // Given

        // When
        final Order result = orderService.accept(orderStatusWaiting.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 상태 변경 대기 -> 수락 실패")
    @Test
    void changeOrderAcceptFail() {
        // Given

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(eatInOrderStatusCompleted.getId()));
    }

    @DisplayName("주문 상태 변경 수락 -> 제공 성공")
    @Test
    void changeOrderServeSuccess() {
        // Given

        // When
        final Order result = orderService.serve(orderStatusAccepted.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 상태 변경 수락 -> 제공 실패")
    @Test
    void changeOrderServeFail() {
        // Given

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(eatInOrderStatusCompleted.getId()));
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 배달중 성공")
    @Test
    void changeOrderStartDeliverySuccess() {
        // Given

        // When
        final Order result = orderService.startDelivery(orderStatusServed.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 배달중 실패, 배달주문이 아닌 경우")
    @Test
    void changeOrderStartDeliveryFailNotDeliveryOrder() {
        // Given

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(eatInOrderStatusCompleted.getId()));
    }

    @DisplayName("배달주문 상태변경 배달중 -> 배달 완료 성공")
    @Test
    void changeOrderCompleteDeliverySuccess() {
        // Given

        // When
        final Order result = orderService.completeDelivery(orderStatusDelivering.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달주문 상태변경 배달중 -> 배달 완료 실패, 배달중이 아닌 주문")
    @Test
    void changeOrderCompleteDeliveryFailNotDeliveryOrder() {
        // Given

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(orderStatusDelivered.getId()));
    }

    @DisplayName("매장주문 상태변경 주문제공 -> 주문완료 성공")
    @Test
    void changeEatInOrderCompleteSuccess() {
        // Given

        // When
        final Order result = orderService.complete(eatInOrderStatusCompleted.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 주문완료 성공")
    @Test
    void changeDeliveryOrderCompleteSuccess() {
        // Given

        // When
        final Order result = orderService.complete(orderStatusDelivered.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 주문완료 샐패, 배달주문의 주문상태가 배달완료가 아닌 경우")
    @Test
    void changeDeliveryOrderCompleteFailOrderStateNotDelivered() {
        // Given

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(orderStatusDelivering.getId()));
    }

    @DisplayName("매장주 상태변경 주문제공 -> 주문완료 샐패, 배달주문의 주문상태가 배달완료가 아닌 경우")
    @Test
    void changeEatInOrderCompleteFailOrderStateNotServed() {
        // Given

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(eatInOrderStatusCompleted.getId()));
    }

    @DisplayName("전체 주문 조회")
    @Test
    void findAll() {
        // Given

        // When
        final List<Order> list = orderService.findAll();

        // Then
        assertThat(list).isNotEmpty();
    }

}
