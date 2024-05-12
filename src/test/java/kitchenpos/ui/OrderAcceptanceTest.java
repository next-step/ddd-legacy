package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.acceptacne.steps.MenuGroupSteps.createMenuGroupStep;
import static kitchenpos.acceptacne.steps.MenuSteps.createMenuStep;
import static kitchenpos.acceptacne.steps.OrderSteps.acceptOrderStep;
import static kitchenpos.acceptacne.steps.OrderSteps.completeDeliveryOrderStep;
import static kitchenpos.acceptacne.steps.OrderSteps.completeOrderStep;
import static kitchenpos.acceptacne.steps.OrderSteps.createOrderStep;
import static kitchenpos.acceptacne.steps.OrderSteps.serveOrderStep;
import static kitchenpos.acceptacne.steps.OrderSteps.startDeliveryOrderStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.createOrderTableStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.sitOrderTableStep;
import static kitchenpos.acceptacne.steps.ProductSteps.createProductStep;
import static kitchenpos.fixture.MenuFixture.이름_반반치킨;
import static kitchenpos.fixture.MenuFixture.가격_38000;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.OrderFixture.orderDeliveryCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderEatInCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderTakeOutCreateRequest;
import static kitchenpos.fixture.OrderFixture.배달주소;
import static kitchenpos.fixture.OrderLineItemFixture.orderLineItemCreate;
import static kitchenpos.fixture.OrderTableFixture.이름_1번;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 인수테스트")
@AcceptanceTest
class OrderAcceptanceTest {
    private UUID ORDER_TABLE_ID;
    private Order ORDER_매장주문;
    private Order ORDER_배달주문;
    private Order ORDER_포장주문;
    @BeforeEach
    void setUp() {
        MenuGroup menuGroup = createMenuGroup();
        MenuProduct menuProduct_양념치킨 = createMenuProduct(이름_양념치킨, 가격_20000, 1);
        MenuProduct menuProduct_후라이드 = createMenuProduct(이름_후라이드치킨, 가격_18000, 1);
        Menu menu = createMenu(가격_38000, menuGroup, menuProduct_양념치킨, menuProduct_후라이드);
        OrderTable orderTable = createOrderTable();
        OrderLineItem ORDER_ITEM_주문메뉴항목 = orderLineItemCreate(menu, 가격_38000, 1);
        ORDER_TABLE_ID = orderTable.getId();
        ORDER_매장주문 = orderEatInCreateRequest(ORDER_TABLE_ID, ORDER_ITEM_주문메뉴항목);
        ORDER_배달주문 = orderDeliveryCreateRequest(배달주소, ORDER_ITEM_주문메뉴항목);
        ORDER_포장주문 = orderTakeOutCreateRequest(ORDER_ITEM_주문메뉴항목);
    }

    @Nested
    @DisplayName("주문 등록 테스트")
    class OrderCreate {
        @DisplayName("매장주문이 들어왔습니다.")
        @Test
        void createEetInOrder() {
            // given
            sitOrderTableStep(ORDER_TABLE_ID);

            // when
            ExtractableResponse<Response> response = createOrderStep(ORDER_매장주문);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.EAT_IN.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.WAITING.name()),
                    () -> assertThat(response.jsonPath().getString("orderDateTime")).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("deliveryAddress")).isNull(),
                    () -> assertThat(response.jsonPath().getObject("orderTable.id", UUID.class)).isEqualTo(ORDER_TABLE_ID),
                    () -> assertThat(response.jsonPath().getInt("orderTable.numberOfGuests")).isZero(),
                    () -> assertThat(response.jsonPath().getBoolean("orderTable.occupied")).isTrue(),
                    () -> assertThat(response.jsonPath().getList("orderLineItems.menu.id")).hasSizeGreaterThanOrEqualTo(1),
                    () -> assertThat(response.jsonPath().getList("orderLineItems.quantity")).containsExactly(1),
                    () -> assertThat(response.jsonPath().getList("orderLineItems.menu.price")).containsExactly(가격_38000.floatValue()),
                    () -> assertThat(response.jsonPath().getList("orderLineItems.menu.displayed")).containsExactly(true)
            );
        }

        @DisplayName("배달주문이 들어왔습니다.")
        @Test
        void createDeliveryOrder() {
            // given
            // when
            ExtractableResponse<Response> response = createOrderStep(ORDER_배달주문);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.WAITING.name()),
                    () -> assertThat(response.jsonPath().getString("orderDateTime")).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("deliveryAddress")).isEqualTo(배달주소),
                    () -> assertThat(response.jsonPath().getObject("orderTable", OrderTable.class)).isNull()
            );
        }

        @DisplayName("포장주문이 들어왔습니다.")
        @Test
        void createTakeOutOrder() {
            // given
            // when
            ExtractableResponse<Response> response = createOrderStep(ORDER_포장주문);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.TAKEOUT.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.WAITING.name()),
                    () -> assertThat(response.jsonPath().getString("orderDateTime")).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("deliveryAddress")).isNull(),
                    () -> assertThat(response.jsonPath().getObject("orderTable", OrderTable.class)).isNull()
            );
        }
    }
    
    @Nested
    @DisplayName("주문수락 테스트")
    class AcceptOrder {
        @DisplayName("매장주문을 수락합니다.")
        @Test
        void acceptEetInOrder() {
            // given
            sitOrderTableStep(ORDER_TABLE_ID);
            UUID orderId = createOrderId(ORDER_매장주문);

            // when
            ExtractableResponse<Response> response = acceptOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.EAT_IN.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.ACCEPTED.name())
            );
        }

        @DisplayName("배달주문을 수락합니다.")
        @Test
        void acceptDeliveryOrder() {
            // given
            UUID orderId = createOrderId(ORDER_배달주문);

            // when
            ExtractableResponse<Response> response = acceptOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.ACCEPTED.name())
            );
        }


        @DisplayName("매장주문을 수락합니다.")
        @Test
        void acceptTakeOutOrder() {
            // given
            UUID orderId = createOrderId(ORDER_포장주문);

            // when
            ExtractableResponse<Response> response = acceptOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.TAKEOUT.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.ACCEPTED.name())
            );
        }
    }

    @Nested
    @DisplayName("제조완료 테스트")
    class ServedOrder {
        @DisplayName("매장 주문의 제조를 완료합니다.")
        @Test
        void serveEetInOrder() {
            // given
            sitOrderTableStep(ORDER_TABLE_ID);
            UUID orderId = createOrderId(ORDER_매장주문);
            acceptOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = serveOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.EAT_IN.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.SERVED.name())
            );
        }

        @DisplayName("배달주문의 제조를 완료합니다.")
        @Test
        void serveDeliveryOrder() {
            // given
            UUID orderId = createOrderId(ORDER_배달주문);
            acceptOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = serveOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.SERVED.name())
            );
        }

        @DisplayName("포장주문의 제조를 완료합니다.")
        @Test
        void serveTakeOutOrder() {
            // given
            UUID orderId = createOrderId(ORDER_포장주문);
            acceptOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = serveOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.TAKEOUT.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.SERVED.name())
            );
        }
    }

    @Nested
    @DisplayName("배달 테스트")
    class DeliveryOrder {
        @DisplayName("배달을 시작합니다.")
        @Test
        void startDelivery_DeliveryOrder() {
            // given
            UUID orderId = createOrderId(ORDER_배달주문);
            acceptOrderStep(orderId);
            serveOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = startDeliveryOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.DELIVERING.name())
            );
        }

        @DisplayName("배달이 완료됩니다.")
        @Test
        void completeDelivery_DeliveryOrder() {
            // given
            UUID orderId = createOrderId(ORDER_배달주문);
            acceptOrderStep(orderId);
            serveOrderStep(orderId);
            startDeliveryOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = completeDeliveryOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.DELIVERED.name())
            );
        }
    }

    @Nested
    @DisplayName("주문종료 테스트")
    class CompleteOrder {
        @DisplayName("매장주문을 종료합니다.")
        @Test
        void completeEetInOrder() {
            // given
            sitOrderTableStep(ORDER_TABLE_ID);
            UUID orderId = createOrderId(ORDER_매장주문);
            acceptOrderStep(orderId);
            serveOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = completeOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.EAT_IN.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.COMPLETED.name()),
                    () -> assertThat(response.jsonPath().getInt("orderTable.numberOfGuests")).isZero(),
                    () -> assertThat(response.jsonPath().getBoolean("orderTable.occupied")).isFalse()
            );
        }

        @DisplayName("배달주문을 종료합니다.")
        @Test
        void completeDeliveryOrder() {
            // given
            UUID orderId = createOrderId(ORDER_배달주문);
            acceptOrderStep(orderId);
            serveOrderStep(orderId);
            startDeliveryOrderStep(orderId);
            completeDeliveryOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = completeOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.COMPLETED.name())
            );
        }

        @DisplayName("포장주문을 종료합니다.")
        @Test
        void completeTakeOutOrder() {
            // given
            UUID orderId = createOrderId(ORDER_포장주문);
            acceptOrderStep(orderId);
            serveOrderStep(orderId);

            // when
            ExtractableResponse<Response> response = completeOrderStep(orderId);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("type")).isEqualTo(OrderType.TAKEOUT.name()),
                    () -> assertThat(response.jsonPath().getString("status")).isEqualTo(OrderStatus.COMPLETED.name())
            );
        }
    }

    private static OrderTable createOrderTable() {
        OrderTable orderTable = orderTableCreateRequest(이름_1번);
        return createOrderTableStep(orderTable).as(OrderTable.class);
    }

    private static Menu createMenu(BigDecimal price, MenuGroup menuGroup, MenuProduct... menuProducts) {
        Menu menu = menuCreateRequest(이름_반반치킨, price, menuGroup.getId(), true, menuProducts);
        return createMenuStep(menu).as(Menu.class);
    }

    @NotNull
    private static MenuProduct createMenuProduct(String name, BigDecimal price, long quantity) {
        Product product = productCreateRequest(name, price);
        Product productResponse = createProductStep(product).as(Product.class);
        return menuProductResponse(productResponse, quantity);
    }

    private static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = menuGroupCreateRequest(이름_추천메뉴);
        return createMenuGroupStep(menuGroup).as(MenuGroup.class);
    }

    private static UUID createOrderId(Order request) {
        return createOrderStep(request).as(Order.class).getId();
    }
}
