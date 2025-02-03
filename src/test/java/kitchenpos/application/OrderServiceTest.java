package kitchenpos.application;

import org.junit.jupiter.api.Test;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private OrderService orderService;
    private Menu menu;
    private MenuGroup menuGroup;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        menuRepository.deleteAll();
        orderTableRepository.deleteAll();
        menuGroupRepository.deleteAll();

        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        menuGroupRepository.save(menuGroup);

        menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setDisplayed(true);
        menuRepository.save(menu);

        orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(4);
        orderTableRepository.save(orderTable);
    }
    @DisplayName("배달 주문을 생성할 수 있다.")
    @Test
    void createDeliveryOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setDeliveryAddress("삼성역");
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));

        // when
        Order result = orderService.create(order);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        Assertions.assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
    }

    @DisplayName("매장 식사 주문을 생성할 수 있다.")
    @Test
    void createEatInOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));

        // when
        Order result = orderService.create(order);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        Assertions.assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
    }

    @DisplayName("포장 주문을 생성할 수 있다.")
    @Test
    void createTakeOutOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));

        // when
        Order result = orderService.create(order);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        Assertions.assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
    }

    @DisplayName("주문을 생성 시, 주문 타입이 없으면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotCreateOrderWithoutOrderType() {
        //given
        Order order = new Order();
        order.setType(null);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));

    }

    @DisplayName("주문을 생성 시, 주문 항목이 존재하지 않으면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotCreateOrderWithOutOrderLineItem() {
        //given
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(null);
        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));

    }

    @DisplayName("주문을 생성 시, 존재하지 않은 메뉴를 주문하면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotCreateOrderIfMenuDoesNotExist() {
        //given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(UUID.randomUUID());
        order.setOrderLineItems(List.of(orderLineItem));

        //when

        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));

    }


    @DisplayName("주문을 생성 시, 매장 식사 이외의 경우 주문 항목 수량이 0보다 작으면 IllegalArgumentException 예외를 발생한다.")
    @Test
    void canNotCreateOrderIfQuantityOfOrderLineItemUnderZeroWhenExceptEatIn() {
        //given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(-1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(orderLineItem));

        //when

        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));

    }


    @DisplayName("주문을 생성 시, 주문항목에 포함된 메뉴가 판매 불가능 상태일 경우 IllegalStateException 예외를 발생한다.")
    @Test
    void canNotCreateOrderIfMenuOfOrderLineItemIsNotDisplayed() {
        //given
        OrderLineItem orderLineItem = new OrderLineItem();
        menu.setDisplayed(false);
        menuRepository.save(menu);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(orderLineItem));

        //when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));

    }

    @DisplayName("주문을 생성 시, 주문 항목의 가격이 실제 메뉴 가격과 다르면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void canNotCreateOrderIfOrderLineItemPriceIsDifferentFromMenuPrice() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(18000));

        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(orderLineItem));

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order)); // ✅ 예외 발생 검증
    }

    @DisplayName("배달 주문 시, 배송 주소가 null이면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void canNotCreateDeliveryOrderIfDeliveryAddressIsNull() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        // when

        // then
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress(null);
        order.setOrderLineItems(List.of(orderLineItem));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));

    }
    @DisplayName("배달 주문 시, 배송 주소가 비어 있으면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void canNotCreateDeliveryOrderIfDeliveryAddressIsEmpty() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        // when

        // then
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("");
        order.setOrderLineItems(List.of(orderLineItem));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }


    @DisplayName("매장 식사 주문 시, 테이블이 존재하지 않으면 아니면 NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotCreateEatInOrderIfOrderTableDoesNotExist() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order orderTable = new Order();
        orderTable.setType(OrderType.EAT_IN);
        orderTable.setOrderTableId(UUID.randomUUID());
        orderTable.setOrderLineItems(List.of(orderLineItem));

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(orderTable));
    }

    @DisplayName("매장 식사 주문 시, 테이블이 미사용 상태이면 IllegalStateException 예외가 발생한다.")
    @Test
    void canNotCreateEatInOrderIfOrderTableIsNotOccupied() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        orderTable.setOccupied(false);
        orderTableRepository.save(orderTable);

        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(List.of(orderLineItem));

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문을 수락 할 수 있다.")
    @Test
    void accept() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.WAITING);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        //when
        Order result = orderService.accept(order.getId());

        //when
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문을 수할 시, 존재하지 않은 주문을 승인할 경우 NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotAcceptIfNonExistOrder() {
        // given
        UUID nonExistId = UUID.randomUUID();

        // when

        // then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.accept(nonExistId));
    }

    @DisplayName("주문을 수락 시, 대기가 아닌 주문을 승인할 경우 IllegalStateException 예외가 발생한다.")
    @Test
    void canNotAcceptNonWaitingOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(order.getId()));
    }


    @DisplayName("주문을 제공 할 수 있다.")
    @Test
    void serve() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        //when
        Order result = orderService.serve(order.getId());

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문을 제공 시, 존재하지 않은 주문을 승인할 경우 NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotServeIfNonExistOrder() {
        // given
        UUID nonExistId = UUID.randomUUID();

        // when

        // then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.serve(nonExistId));
    }

    @DisplayName("주문을 제공 시, 주문 상태가 수락이 아닐 경우 IllegalStateException 예외가 발생한다.")
    @Test
    void canNotServeIfOrderStatusIsNotAccepted() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("배달을 시작 할 수 있다.")
    @Test
    void startDelivery() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when
        Order result = orderService.startDelivery(order.getId());

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 주문 시, 주문 타입이 배달이 아닐 경우 IllegalStateException 예외가 발생한다.")
    @Test
    void canNotStartDeliveryIfNotDeliveryType() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("배달 주문 시, 주문 상태가 제공 됨이 아닐 경우 IllegalStateException 예외가 발생한다.")
    @Test
    void canNotStartDeliveryIfNotServedStatus() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("배달을 완료 할 수 있다.")
    @Test
    void completeDelivery() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("삼성역");
        order.setStatus(OrderStatus.DELIVERING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when
        Order result = orderService.completeDelivery(order.getId());

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("존재하지 않는 주문을 배달 완료 시도 할 경우 NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotCompleteDeliveryWithNotExistOrder() {
        //given
        UUID nonExistId = UUID.randomUUID();

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.completeDelivery(nonExistId));
    }

    @DisplayName("배달 진행 중이 아닌 주문을 배달 완료하려고 하면 IllegalStateException 예외가 발생한다.")
    @Test
    void cannotCompleteDeliveryIfNotDelivering() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("삼성역");
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when


        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }


    @DisplayName("배달 주문을 완료 할 수 있다.")
    @Test
    void completeDeliveryOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveryAddress("삼성역");
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when
        Order result = orderService.complete(order.getId());

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장 주문을 완료 할 수 있다.")
    @Test
    void completeTakeOutAndEatInOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when
        Order result = orderService.complete(order.getId());

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장식사 주문을 완료 할 수 있다.")
    @Test
    void completeEatInOrder() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when
        Order result = orderService.complete(order.getId());

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("존재하지 앟는 주문을 완료하면, NoSuchElementException 예외가 발생한다.")
    @Test
    void canNotCompleteIfNotExist() {
        // given
        UUID notExistId = UUID.randomUUID();

        // when

        // then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.complete(notExistId));
    }

    @DisplayName("배달 주문이 배달 완료 상태가 아니면 IllegalStateException 예외가 발생한다.")
    @Test
    void canNotCompleteDeliveryOrderIfNotDelivered() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("삼성역");
        order.setStatus(OrderStatus.DELIVERING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("매장 식사 또는 포장 주문이 제공 완료 상태가 아니면, IllegalStateException 예외가 발생한다.")
    @Test
    void canNotCompleteTakeoutOrEatInOrderIfNotServed() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when

        // then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("매장 식사 주문 완료 시, 테이블에 진행 중인 주문이 없으면 테이블 상태를 초기화한다.")
    @Test
    void completeEatInOrderAndResetTableIfNoPendingOrders() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(16000));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        orderRepository.save(order);

        // when
        orderService.complete(order.getId());

        // then
        OrderTable result = orderTableRepository.findById(orderTable.getId()).get();
        Assertions.assertThat(result.isOccupied()).isFalse();
        Assertions.assertThat(result.getNumberOfGuests()).isZero();
    }



    @DisplayName("모든 주문을 조회 할 수 있다.")
    @Test
    void findAll() {
        // given
        OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setMenu(menu);
        orderLineItem1.setMenuId(menu.getId());
        orderLineItem1.setQuantity(1);
        orderLineItem1.setPrice(BigDecimal.valueOf(16000));

        Order order1 = new Order();
        order1.setId(UUID.randomUUID());
        order1.setType(OrderType.DELIVERY);
        order1.setStatus(OrderStatus.WAITING);
        order1.setDeliveryAddress("삼성역");
        order1.setOrderDateTime(LocalDateTime.now());
        order1.setOrderLineItems(List.of(orderLineItem1));
        order1 = orderRepository.save(order1);

        OrderLineItem orderLineItem2 = new OrderLineItem();
        orderLineItem2.setMenu(menu);
        orderLineItem2.setMenuId(menu.getId());
        orderLineItem2.setQuantity(2);
        orderLineItem2.setPrice(BigDecimal.valueOf(32000));

        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setType(OrderType.TAKEOUT);
        order2.setStatus(OrderStatus.WAITING);
        order2.setOrderDateTime(LocalDateTime.now());
        order2.setOrderLineItems(List.of(orderLineItem2));
        order2 = orderRepository.save(order2);

        // when
        List<Order> result = orderService.findAll();

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).extracting(Order::getId).containsExactlyInAnyOrder(order1.getId(), order2.getId());
    }
}