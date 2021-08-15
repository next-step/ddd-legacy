package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.application.fixture.MenuFixture.*;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_ONE_REQUEST;
import static kitchenpos.application.fixture.OrderFixture.*;
import static kitchenpos.application.fixture.OrderLineItemFixture.ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderTableFixture.EMPTY_ORDER_TABLE_REQUEST;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_ORDER_TABLE_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT_ONE_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @DisplayName("주문 등록 성공")
    @Test
    void createOrderSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(NOT_EMPTY_ORDER_TABLE_REQUEST());

        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(NOT_EMPTY_ORDER_TABLE_REQUEST().getId());
        request.setOrderLineItems(ORDER_LINE_ITEMS());

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
        request.setType(null);
        request.setOrderTableId(NOT_EMPTY_ORDER_TABLE_REQUEST().getId());
        request.setOrderLineItems(ORDER_LINE_ITEMS());

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 주문 목록이 없는 경우")
    @Test
    void createOrderFailOrderLineItemsIsNull() {
        // Given
        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(NOT_EMPTY_ORDER_TABLE_REQUEST().getId());

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 등록되지 않은 메뉴를 주문한 경우")
    @Test
    void createOrderFailNonExistentMenu() {
        // Given
        final OrderLineItem noneExistMenu = new OrderLineItem();
        noneExistMenu.setMenuId(UUID.randomUUID());

        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(NOT_EMPTY_ORDER_TABLE_REQUEST().getId());
        request.setOrderLineItems(Arrays.asList(noneExistMenu));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 매장식사가 아닌데 주문목록의 수량이 음수인 경우")
    @Test
    void createOrderFailOrderLineItemMinusQuantity() {
        // Given
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(SHOW_MENU_REQUEST().getId());
        orderLineItem.setPrice(SHOW_MENU_REQUEST().getPrice());
        orderLineItem.setQuantity(-1);

        final Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderTableId(NOT_EMPTY_ORDER_TABLE_REQUEST().getId());
        request.setOrderLineItems(Arrays.asList(orderLineItem));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 등록 실패 - 숨겨진 메뉴를 주문할 경우")
    @Test
    void createOrderFailOrderLineItemHideMenu() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(HIDE_MENU_REQUEST());

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(HIDE_MENU_REQUEST().getId());
        orderLineItem.setPrice(HIDE_MENU_REQUEST().getPrice());
        orderLineItem.setQuantity(1);

        final Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderTableId(NOT_EMPTY_ORDER_TABLE_REQUEST().getId());
        request.setOrderLineItems(Arrays.asList(orderLineItem));

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
        request.setOrderTableId(EMPTY_ORDER_TABLE_REQUEST().getId());
        request.setOrderLineItems(ORDER_LINE_ITEMS());

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 상태 변경 대기 -> 수락 성공")
    @Test
    void changeOrderAcceptSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(NORMAL_ORDER_REQUEST());

        // When
        final Order result = orderService.accept(request.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(order.getId()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        );
    }

    @DisplayName("주문 상태 변경 대기 -> 수락 실패")
    @Test
    void changeOrderAcceptFail() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(EAT_IN_ORDER_STATUS_COMPLETED_REQUEST());

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(request.getId()));
    }

    @DisplayName("주문 상태 변경 수락 -> 제공 성공")
    @Test
    void changeOrderServeSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(EAT_IN_ORDER_STATUS_ACCEPTED_REQUEST());

        // When
        final Order result = orderService.serve(request.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(order.getId()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
        );
    }

    @DisplayName("주문 상태 변경 수락 -> 제공 실패")
    @Test
    void changeOrderServeFail() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(EAT_IN_ORDER_STATUS_SERVE_REQUEST());

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(request.getId()));
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 배달중 변경 성공")
    @Test
    void changeOrderStartDeliverySuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(DELIVERY_ORDER_STATUS_SERVE_REQUEST());

        // When
        final Order result = orderService.startDelivery(request.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(order.getId()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING)
        );
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 배달중 변경 실패, 배달주문이 아닌 경우")
    @Test
    void changeOrderStartDeliveryFailNotDeliveryOrder() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(EAT_IN_ORDER_STATUS_COMPLETED_REQUEST());

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(request.getId()));
    }

    @DisplayName("배달주문 상태변경 배달중 -> 배달완료 변경 성공")
    @Test
    void changeOrderCompleteDeliverySuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(DELIVERY_ORDER_STATUS_DELIVERING_REQUEST());

        // When
        final Order result = orderService.completeDelivery(request.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(order.getId()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED)
        );
    }

    @DisplayName("배달주문 상태변경 배달중 -> 배달완료 변경 실패, 배달중이 아닌 주문")
    @Test
    void changeOrderCompleteDeliveryFailNotDeliveryOrder() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(DELIVERY_ORDER_STATUS_DELIVERED_REQUEST());

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(request.getId()));
    }

    @DisplayName("매장주문 상태변경 주문제공 -> 주문완료 성공")
    @Test
    void changeEatInOrderCompleteSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(EAT_IN_ORDER_STATUS_SERVE_REQUEST());

        // When
        final Order result = orderService.complete(request.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(order.getId()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 주문완료 성공")
    @Test
    void changeDeliveryOrderCompleteSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(DELIVERY_ORDER_STATUS_DELIVERED_REQUEST());

        // When
        final Order result = orderService.complete(request.getId());

        // Then
        final Order order = orderRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(order.getId()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    @DisplayName("배달주문 상태변경 주문제공 -> 주문완료 변경 실패, 배달주문의 주문상태가 배달완료가 아닌 경우")
    @Test
    void changeDeliveryOrderCompleteFailOrderStateNotDelivered() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(DELIVERY_ORDER_STATUS_DELIVERING_REQUEST());

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(request.getId()));
    }

    @DisplayName("매장주문 상태변경 주문제공 -> 주문완료 변경 실패, 매장주문의 상태가 주문제공 아닌 경우")
    @Test
    void changeEatInOrderCompleteFailOrderStateNotServed() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());
        orderTableRepository.save(EMPTY_ORDER_TABLE_REQUEST());

        Order request = orderRepository.save(EAT_IN_ORDER_STATUS_ACCEPTED_REQUEST());

        // When, Then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(request.getId()));
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
