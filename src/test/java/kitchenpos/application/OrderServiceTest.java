package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.integration_test_step.MenuIntegrationStep;
import kitchenpos.integration_test_step.OrderIntegrationStep;
import kitchenpos.integration_test_step.OrderTableIntegrationStep;
import kitchenpos.test_fixture.MenuTestFixture;
import kitchenpos.test_fixture.OrderLineItemTestFixture;
import kitchenpos.test_fixture.OrderTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@DisplayName("OrderService 클래스")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService sut;

    @Autowired
    private OrderIntegrationStep orderIntegrationStep;

    @Autowired
    private MenuIntegrationStep menuIntegrationStep;

    @Autowired
    private OrderTableIntegrationStep orderTableIntegrationStep;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    @Autowired
    private DatabaseCleanStep databaseCleanStep;

    @DisplayName("새로운 주문 생성")
    @Nested
    class Describe_create {

        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("새로운 주문을 생성할 수 있다.")
        @Test
        void create() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when
            Order result = sut.create(order);

            // then
            assertThat(result).isNotNull();
        }

        @DisplayName("새로운 주문 생성 시 주문 상태는 대기(WAITING) 상태이다.")
        @Test
        void createOrderStatusWaiting() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when
            Order result = sut.create(order);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @DisplayName("새로운 주문 생성 시 주문 유형이 비어있으면 예외가 발생한다.")
        @Test
        void createOrderTypeNullExceptionThrown() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(null)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }

        @DisplayName("새로운 주문 생성 시 주문 메뉴가 비어있으면 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createOrderLineItemsNullExceptionThrown(List<OrderLineItem> orderLineItems) {
            // given
            Menu menu = menuIntegrationStep.create();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(orderLineItems)
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }

        @DisplayName("새로운 주문 생성 시 주문 메뉴가 존재하지 않는 메뉴면 예외가 발생한다.")
        @Test
        void createOrderLineItemsMenuNotFoundExceptionThrown() {
            // given
            Menu notPersistMenu = MenuTestFixture.create().getMenu();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(notPersistMenu)
                    .changePrice(notPersistMenu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }

        @DisplayName("새로운 주문 생성 시 주문 유형이 매장 식사가 아니면, 각 주문 메뉴의 수량은 음수가 될 수 없다.")
        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
        void createOrderLineItemsQuantityExceptionThrown(OrderType orderType) {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .changeQuantity(-1L)
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(orderType)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }

        @DisplayName("새로운 주문 생성 시 주문 유형이 매장 식사이면, 각 주문 메뉴의 수량은 음수를 허용한다.")
        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"EAT_IN"})
        void createOrderLineItemsQuantity(OrderType orderType) {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderTable orderTable = orderTableIntegrationStep.createSitTable();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .changeQuantity(-1L)
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(orderType)
                    .changeOrderTable(orderTable)
                    .changeOrderTableId(orderTable)
                    .getOrder();

            // when
            Order result = assertDoesNotThrow(() -> sut.create(order));

            // then
            assertThat(result).isNotNull();
        }

        @DisplayName("새로운 주문 생성 시 주문 메뉴가 숨김 상태라면 예외가 발생한다.")
        @Test
        void createOrderLineItemsMenuHiddenExceptionThrown() {
            // given
            Menu menu = menuIntegrationStep.createHideMenu();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.create(order));
        }

        @DisplayName("주문 메뉴는 실제 메뉴와 가격 동일하지 않으면 예외가 발생한다.")
        @Test
        void createOrderLineItemsPriceExceptionThrown() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice().add(BigDecimal.ONE))
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }

        @DisplayName("새로운 배달 주문은 배달주소가 비어있으면 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createDeliveryAddressNullExceptionThrown(String deliveryAddress) {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .changeDeliveryAddress(deliveryAddress)
                    .getOrder();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(order));
        }

        @DisplayName("배달 주문은 주문 테이블 정보가 비어있어도 주문이 가능하다.")
        @Test
        void createDeliveryOrderTableNull() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.DELIVERY)
                    .changeOrderTable(null)
                    .getOrder();

            // when & then
            assertDoesNotThrow(() -> sut.create(order));
        }

        @DisplayName("매장 식사 주문에 등록하려는 주문 테이블이 존재하지 않는 주문 테이블이면 예외가 발생한다.")
        @Test
        void createEatInOrderTableNull() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createSitTable();
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.EAT_IN)
                    .changeOrderTable(null)
                    .getOrder();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.create(order));
        }

        @DisplayName("매장 식사 주문에 등록하려는 주문 테이블이 비어있는 상태이면 예외가 발생한다.")
        @Test
        void createEatInOrderTableEmpty() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createEmptyTable();
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();
            Order order = OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.EAT_IN)
                    .changeOrderTable(orderTable)
                    .changeOrderTableId(orderTable)
                    .getOrder();

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.create(order));
        }

        @DisplayName("매장 식사 주문의 배달 주소 값은 필요하지 않다.")
        @Test
        void createEatInDeliveryAddressNull() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createSitTable();
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();

            // when & then
            assertDoesNotThrow(() -> sut.create(OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.EAT_IN)
                    .changeOrderTable(orderTable)
                    .changeOrderTableId(orderTable)
                    .changeDeliveryAddress(null)
                    .getOrder()));
        }

        @DisplayName("포장 주문은 배달 주소, 주문 테이블 정보가 필요하지 않다.")
        @Test
        void createTakeOut() {
            // given
            Menu menu = menuIntegrationStep.create();
            OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                    .changeMenu(menu)
                    .changeQuantity(1L)
                    .changePrice(menu.getPrice())
                    .getOrderLineItem();

            // when & then
            assertDoesNotThrow(() -> sut.create(OrderTestFixture.create()
                    .changeId(null)
                    .changeOrderLineItems(Collections.singletonList(orderLineItem))
                    .changeType(OrderType.TAKEOUT)
                    .changeOrderTable(null)
                    .changeDeliveryAddress(null)
                    .getOrder()));
        }
    }

    @DisplayName("주문 상태 수락으로 변경")
    @Nested
    class Describe_accept {

        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("주문 상태를 수락으로 변경할 수 있다.")
        @Test
        void accept() {
            // given
            Order order = orderIntegrationStep.createStatusWaiting();

            // when
            Order result = sut.accept(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("주문 상태를 수락으로 변경할 때 주문이 존재하지 않으면 예외가 발생한다.")
        @Test
        void acceptOrderNotFoundExceptionThrown() {
            // given
            Order notPersistOrder = OrderTestFixture.create()
                    .changeId(UUID.randomUUID())
                    .getOrder();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.accept(notPersistOrder.getId()));
        }

        @DisplayName("주문 상태를 수락으로 변경할 때 주문 상태가 대기 상태가 아니면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void acceptOrderStatusNotWaitingExceptionThrown(OrderStatus orderStatus) {
            // given
            Order order = orderIntegrationStep.createStatus(orderStatus);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.accept(order.getId()));
        }

        @DisplayName("배달 주문이라면 주문 상태를 수락으로 변경할 때 (주문고유 번호, 주문의 가격, 배달주소) 정보들을 배달기사 배정 회사에 배달을 요청해야한다.")
        @Test
        void acceptDeliveryOrder() {
            // given
            Order order = orderIntegrationStep.createWaitingDeliveryOrder();
            BigDecimal orderPrice = order.getOrderLineItems().stream()
                    .map(orderLineItem -> orderLineItem.getMenu().getPrice().multiply(BigDecimal.valueOf(orderLineItem.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            doNothing().when(kitchenridersClient).requestDelivery(order.getId(), orderPrice, order.getDeliveryAddress());
            // when
            Order result = sut.accept(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(kitchenridersClient).requestDelivery(order.getId(), orderPrice, order.getDeliveryAddress());
        }
    }

    @DisplayName("주문 상태 제공으로 변경")
    @Nested
    class Describe_served {
        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("주문 상태를 제공으로 변경할 수 있다.")
        @Test
        void served() {
            // given
            Order order = orderIntegrationStep.createStatusAccept();

            // when
            Order result = sut.serve(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @DisplayName("주문 상태를 제공으로 변경할 때 주문이 존재하지 않으면 예외가 발생한다.")
        @Test
        void servedOrderNotFoundExceptionThrown() {
            // given
            Order notPersistOrder = OrderTestFixture.create()
                    .changeId(UUID.randomUUID())
                    .getOrder();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.serve(notPersistOrder.getId()));
        }

        @DisplayName("주문 상태를 제공으로 변경할 때 주문 상태가 수락 상태가 아니면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void servedOrderStatusNotAcceptedExceptionThrown(OrderStatus orderStatus) {
            // given
            Order order = orderIntegrationStep.createStatus(orderStatus);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.serve(order.getId()));
        }
    }

    @DisplayName("주문 상태를 배달 중으로 변경")
    @Nested
    class Describe_start_delivery {
        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("주문 상태를 배달 중으로 변경할 수 있다.")
        @Test
        void startDelivery() {
            // given
            Order order = orderIntegrationStep.createServedDeliveryOrder();

            // when
            Order result = sut.startDelivery(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("주문 상태를 배달 중으로 변경할 때 주문이 존재하지 않으면 예외가 발생한다.")
        @Test
        void startDeliveryOrderNotFoundExceptionThrown() {
            // given
            Order notPersistOrder = OrderTestFixture.create()
                    .changeId(UUID.randomUUID())
                    .getOrder();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.startDelivery(notPersistOrder.getId()));
        }

        @DisplayName("주문 상태를 배달 중으로 변경할 때 주문 유형이 배달이 아니면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
        void startDeliveryOrderTypeNotDeliveryExceptionThrown(OrderType orderType) {
            // given
            Order order = orderIntegrationStep.createServedOrderByType(orderType);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.startDelivery(order.getId()));
        }

        @DisplayName("주문 상태를 배달 중으로 변경할 때 주문 상태가 제공 상태가 아니면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "WAITING", "DELIVERING", "DELIVERED", "COMPLETED"})
        void startDeliveryOrderStatusNotServedExceptionThrown(OrderStatus orderStatus) {
            // given
            Order order = orderIntegrationStep.createDeliveryByStatus(orderStatus);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.startDelivery(order.getId()));
        }
    }

    @DisplayName("주문 상태를 배달 완료로 변경")
    @Nested
    class Describe_complete_delivery {
        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("주문 상태를 배달 완료로 변경할 수 있다.")
        @Test
        void completeDelivery() {
            // given
            Order order = orderIntegrationStep.createDeliveryByStatus(OrderStatus.DELIVERING);

            // when
            Order result = sut.completeDelivery(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("주문 상태를 배달 완료로 변경할 때 주문이 존재하지 않으면 예외가 발생한다.")
        @Test
        void completeDeliveryOrderNotFoundExceptionThrown() {
            // given
            Order notPersistOrder = OrderTestFixture.create()
                    .changeId(UUID.randomUUID())
                    .getOrder();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.completeDelivery(notPersistOrder.getId()));
        }

        @DisplayName("주문 상태를 배달 완료로 변경할 때 주문 상태가 배달 중 상태가 아니라면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "WAITING", "SERVED", "DELIVERED", "COMPLETED"})
        void completeDeliveryOrderStatusNotDeliveringExceptionThrown(OrderStatus orderStatus) {
            // given
            Order order = orderIntegrationStep.createDeliveryByStatus(orderStatus);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.completeDelivery(order.getId()));
        }
    }

    @DisplayName("주문 상태를 완료로 변경")
    @Nested
    class Describe_complete {
        @BeforeEach
        void setUp() {
            databaseCleanStep.clean();
        }

        @DisplayName("매장 식사 주문, 포장 주문의 상태를 완료로 변경할 수 있다.")
        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
        void complete(OrderType orderType) {
            // given
            Order order = orderIntegrationStep.createByTypeAndStatus(orderType, OrderStatus.SERVED);

            // when
            Order result = sut.complete(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("배달 주문의 상태를 완료로 변경할 수 있다.")
        @Test
        void complete() {
            // given
            Order order = orderIntegrationStep.createDeliveredDeliveryOrder();

            // when
            Order result = sut.complete(order.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }
}
