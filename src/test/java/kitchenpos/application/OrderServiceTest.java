package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.integration_test_step.MenuIntegrationStep;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OrderService 클래스")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService sut;

    @Autowired
    private MenuIntegrationStep menuIntegrationStep;

    @Autowired
    private OrderTableIntegrationStep orderTableIntegrationStep;

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
    }
}
