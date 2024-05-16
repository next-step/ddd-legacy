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

import static kitchenpos.acceptacne.steps.MenuGroupSteps.메뉴그룹을_등록한다;
import static kitchenpos.acceptacne.steps.MenuSteps.메뉴_등록한다;
import static kitchenpos.acceptacne.steps.OrderSteps.배달을_시작한다;
import static kitchenpos.acceptacne.steps.OrderSteps.배달을_완료한다;
import static kitchenpos.acceptacne.steps.OrderSteps.주문을_등록한다;
import static kitchenpos.acceptacne.steps.OrderSteps.주문을_수락한다;
import static kitchenpos.acceptacne.steps.OrderSteps.주문을_완료한다;
import static kitchenpos.acceptacne.steps.OrderSteps.주문의_제조를_완료한다;
import static kitchenpos.acceptacne.steps.OrderTableSteps.주문테이블에_앉는다;
import static kitchenpos.acceptacne.steps.OrderTableSteps.주문테이블을_등록한다;
import static kitchenpos.acceptacne.steps.ProductSteps.상품을_등록한다;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuFixture.가격_38000;
import static kitchenpos.fixture.MenuFixture.이름_반반치킨;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.OrderFixture.orderDeliveryCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderEatInCreateRequest;
import static kitchenpos.fixture.OrderFixture.orderTakeOutCreateRequest;
import static kitchenpos.fixture.OrderFixture.배달주소;
import static kitchenpos.fixture.OrderLineItemFixture.orderLineItemCreate;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static kitchenpos.fixture.OrderTableFixture.이름_1번;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 인수테스트")
@AcceptanceTest
class OrderAcceptanceTest {
    private UUID 주문테이블Id;
    private Order 매장주문;
    private Order 배달주문;
    private Order 포장주문;

    @BeforeEach
    void setUp() {
        MenuGroup 추천메뉴그룹 = 메뉴그룹을_등록한다(menuGroupCreateRequest(이름_추천메뉴)).as(MenuGroup.class);
        MenuProduct menuProduct_양념치킨 = createMenuProduct(이름_양념치킨, 가격_20000, 1);
        MenuProduct menuProduct_후라이드 = createMenuProduct(이름_후라이드치킨, 가격_18000, 1);
        Menu menu = createMenu(가격_38000, 추천메뉴그룹, menuProduct_양념치킨, menuProduct_후라이드);
        OrderTable orderTable = createOrderTable();
        OrderLineItem ORDER_ITEM_주문메뉴항목 = orderLineItemCreate(menu, 가격_38000, 1);
        주문테이블Id = orderTable.getId();
        매장주문 = orderEatInCreateRequest(주문테이블Id, ORDER_ITEM_주문메뉴항목);
        배달주문 = orderDeliveryCreateRequest(배달주소, ORDER_ITEM_주문메뉴항목);
        포장주문 = orderTakeOutCreateRequest(ORDER_ITEM_주문메뉴항목);
    }

    @Nested
    @DisplayName("주문 등록 테스트")
    class OrderCreate {
        @DisplayName("[성공] 매장주문이 들어왔습니다.")
        @Test
        void createEetInOrder() {
            // given
            주문테이블에_앉는다(주문테이블Id);

            // when
            var 매장주문_등록_응답 = 주문을_등록한다(매장주문);

            // then
            매장주문이_등록되었는지_검증한다(매장주문_등록_응답);
        }

        @DisplayName("[성공] 배달주문이 들어왔습니다.")
        @Test
        void createDeliveryOrder() {
            // given
            // when
            var 배달주문_등록_응답 = 주문을_등록한다(배달주문);

            // then
            배달주문이_등록되었는지_검증한다(배달주문_등록_응답);
        }

        @DisplayName("[성공] 포장주문이 들어왔습니다.")
        @Test
        void createTakeOutOrder() {
            // given
            // when
            var 포장주문_등록_응답 = 주문을_등록한다(포장주문);

            // then
            포장주문이_등록되었는지_검증한다(포장주문_등록_응답);
        }
    }

    @Nested
    @DisplayName("주문수락 테스트")
    class AcceptOrder {
        @DisplayName("[성공] 매장주문을 수락합니다.")
        @Test
        void acceptEetInOrder() {
            // given
            주문테이블에_앉는다(주문테이블Id);
            UUID 매장주문Id = 주문을_등록_후_해당_Id를_반환한다(매장주문);

            // when
            var 매장주문_수락_응답 = 주문을_수락한다(매장주문Id);

            // then
            주문의_종류와_상태를_검증한다(매장주문_수락_응답, OrderType.EAT_IN, OrderStatus.ACCEPTED);
        }

        @DisplayName("[성공] 배달주문을 수락합니다.")
        @Test
        void acceptDeliveryOrder() {
            // given
            UUID 배달주문Id = 주문을_등록_후_해당_Id를_반환한다(배달주문);

            // when
            var 배달주문_수락_응답 = 주문을_수락한다(배달주문Id);

            // then
            주문의_종류와_상태를_검증한다(배달주문_수락_응답, OrderType.DELIVERY, OrderStatus.ACCEPTED);
        }


        @DisplayName("[성공] 매장주문을 수락합니다.")
        @Test
        void acceptTakeOutOrder() {
            // given
            UUID 포장주문Id = 주문을_등록_후_해당_Id를_반환한다(포장주문);

            // when
            var 포장주문_수락_응답 = 주문을_수락한다(포장주문Id);

            // then
            주문의_종류와_상태를_검증한다(포장주문_수락_응답, OrderType.TAKEOUT, OrderStatus.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("제조완료 테스트")
    class ServedOrder {
        @DisplayName("[성공] 매장 주문의 제조를 완료합니다.")
        @Test
        void serveEetInOrder() {
            // given
            주문테이블에_앉는다(주문테이블Id);
            UUID 매장주문Id = 주문을_등록_후_해당_Id를_반환한다(매장주문);
            주문을_수락한다(매장주문Id);

            // when
            var 매장주문_제조완료_응답 = 주문의_제조를_완료한다(매장주문Id);

            // then
            주문의_종류와_상태를_검증한다(매장주문_제조완료_응답, OrderType.EAT_IN, OrderStatus.SERVED);
        }

        @DisplayName("[성공] 배달주문의 제조를 완료합니다.")
        @Test
        void serveDeliveryOrder() {
            // given
            UUID orderId = 주문을_등록_후_해당_Id를_반환한다(배달주문);
            주문을_수락한다(orderId);

            // when
            var 배달주문_제조완료_응답 = 주문의_제조를_완료한다(orderId);

            // then
            주문의_종류와_상태를_검증한다(배달주문_제조완료_응답, OrderType.DELIVERY, OrderStatus.SERVED);
        }

        @DisplayName("[성공] 포장주문의 제조를 완료합니다.")
        @Test
        void serveTakeOutOrder() {
            // given
            UUID 포장주문Id = 주문을_등록_후_해당_Id를_반환한다(포장주문);
            주문을_수락한다(포장주문Id);

            // when
            var 포장주문_제조완료_응답 = 주문의_제조를_완료한다(포장주문Id);

            // then
            주문의_종류와_상태를_검증한다(포장주문_제조완료_응답, OrderType.TAKEOUT, OrderStatus.SERVED);
        }
    }

    @Nested
    @DisplayName("배달 테스트")
    class DeliveryOrder {
        @DisplayName("[성공] 배달을 시작합니다.")
        @Test
        void startDelivery_DeliveryOrder() {
            // given
            UUID 배달주문Id = 주문을_등록_후_해당_Id를_반환한다(배달주문);
            주문을_수락한다(배달주문Id);
            주문의_제조를_완료한다(배달주문Id);

            // when
            var 배달주문_배달시작_응답 = 배달을_시작한다(배달주문Id);

            // then
            주문의_종류와_상태를_검증한다(배달주문_배달시작_응답, OrderType.DELIVERY, OrderStatus.DELIVERING);
        }

        @DisplayName("[성공] 배달이 완료됩니다.")
        @Test
        void completeDelivery_DeliveryOrder() {
            // given
            UUID 배달주문Id = 주문을_등록_후_해당_Id를_반환한다(배달주문);
            주문을_수락한다(배달주문Id);
            주문의_제조를_완료한다(배달주문Id);
            배달을_시작한다(배달주문Id);

            // when
            var 배달주문_배달완료_응답 = 배달을_완료한다(배달주문Id);

            // then
            주문의_종류와_상태를_검증한다(배달주문_배달완료_응답, OrderType.DELIVERY, OrderStatus.DELIVERED);
        }
    }

    @Nested
    @DisplayName("주문종료 테스트")
    class CompleteOrder {
        @DisplayName("[성공] 매장주문을 종료합니다.")
        @Test
        void completeEetInOrder() {
            // given
            주문테이블에_앉는다(주문테이블Id);
            UUID 매장주문Id = 주문을_등록_후_해당_Id를_반환한다(매장주문);
            주문을_수락한다(매장주문Id);
            주문의_제조를_완료한다(매장주문Id);

            // when
            var 매장주문_완료_응답 = 주문을_완료한다(매장주문Id);

            // then
            매장주문의_상태가_주문완료인지_검증한다(매장주문_완료_응답);
        }

        @DisplayName("[성공] 배달주문을 종료합니다.")
        @Test
        void completeDeliveryOrder() {
            // given
            UUID 배달주문Id = 주문을_등록_후_해당_Id를_반환한다(배달주문);
            주문을_수락한다(배달주문Id);
            주문의_제조를_완료한다(배달주문Id);
            배달을_시작한다(배달주문Id);
            배달을_완료한다(배달주문Id);

            // when
            var response = 주문을_완료한다(배달주문Id);

            // then
            주문의_종류와_상태를_검증한다(response, OrderType.DELIVERY, OrderStatus.COMPLETED);
        }

        @DisplayName("[성공] 포장주문을 종료합니다.")
        @Test
        void completeTakeOutOrder() {
            // given
            UUID 포장주문Id = 주문을_등록_후_해당_Id를_반환한다(포장주문);
            주문을_수락한다(포장주문Id);
            주문의_제조를_완료한다(포장주문Id);

            // when
            var 포장주문_완료_응답 = 주문을_완료한다(포장주문Id);

            // then
            주문의_종류와_상태를_검증한다(포장주문_완료_응답, OrderType.TAKEOUT, OrderStatus.COMPLETED);
        }
    }

    private static OrderTable createOrderTable() {
        OrderTable orderTable = orderTableCreateRequest(이름_1번);
        return 주문테이블을_등록한다(orderTable).as(OrderTable.class);
    }

    private static Menu createMenu(BigDecimal price, MenuGroup menuGroup, MenuProduct... menuProducts) {
        Menu menu = menuCreateRequest(이름_반반치킨, price, menuGroup.getId(), true, menuProducts);
        return 메뉴_등록한다(menu).as(Menu.class);
    }

    @NotNull
    private static MenuProduct createMenuProduct(String name, BigDecimal price, long quantity) {
        Product product = productCreateRequest(name, price);
        Product productResponse = 상품을_등록한다(product).as(Product.class);
        return menuProductResponse(productResponse, quantity);
    }

    private static UUID 주문을_등록_후_해당_Id를_반환한다(Order request) {
        return 주문을_등록한다(request).as(Order.class).getId();
    }

    private static void 포장주문이_등록되었는지_검증한다(ExtractableResponse<Response> 포장주문_응답) {
        assertAll(
                () -> assertThat(포장주문_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(포장주문_응답.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(포장주문_응답.jsonPath().getString("type")).isEqualTo(OrderType.TAKEOUT.name()),
                () -> assertThat(포장주문_응답.jsonPath().getString("status")).isEqualTo(OrderStatus.WAITING.name()),
                () -> assertThat(포장주문_응답.jsonPath().getString("orderDateTime")).isNotNull(),
                () -> assertThat(포장주문_응답.jsonPath().getString("deliveryAddress")).isNull(),
                () -> assertThat(포장주문_응답.jsonPath().getObject("orderTable", OrderTable.class)).isNull()
        );
    }

    private static void 배달주문이_등록되었는지_검증한다(ExtractableResponse<Response> 배달주문_응답) {
        assertAll(
                () -> assertThat(배달주문_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(배달주문_응답.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(배달주문_응답.jsonPath().getString("type")).isEqualTo(OrderType.DELIVERY.name()),
                () -> assertThat(배달주문_응답.jsonPath().getString("status")).isEqualTo(OrderStatus.WAITING.name()),
                () -> assertThat(배달주문_응답.jsonPath().getString("orderDateTime")).isNotNull(),
                () -> assertThat(배달주문_응답.jsonPath().getString("deliveryAddress")).isEqualTo(배달주소),
                () -> assertThat(배달주문_응답.jsonPath().getObject("orderTable", OrderTable.class)).isNull()
        );
    }

    private void 매장주문이_등록되었는지_검증한다(ExtractableResponse<Response> 매장주문_등록_검증) {
        assertAll(
                () -> assertThat(매장주문_등록_검증.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(매장주문_등록_검증.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(매장주문_등록_검증.jsonPath().getString("type")).isEqualTo(OrderType.EAT_IN.name()),
                () -> assertThat(매장주문_등록_검증.jsonPath().getString("status")).isEqualTo(OrderStatus.WAITING.name()),
                () -> assertThat(매장주문_등록_검증.jsonPath().getString("orderDateTime")).isNotNull(),
                () -> assertThat(매장주문_등록_검증.jsonPath().getString("deliveryAddress")).isNull(),
                () -> assertThat(매장주문_등록_검증.jsonPath().getObject("orderTable.id", UUID.class)).isEqualTo(주문테이블Id),
                () -> assertThat(매장주문_등록_검증.jsonPath().getInt("orderTable.numberOfGuests")).isZero(),
                () -> assertThat(매장주문_등록_검증.jsonPath().getBoolean("orderTable.occupied")).isTrue(),
                () -> assertThat(매장주문_등록_검증.jsonPath().getList("orderLineItems.menu.id")).hasSizeGreaterThanOrEqualTo(1),
                () -> assertThat(매장주문_등록_검증.jsonPath().getList("orderLineItems.quantity")).containsExactly(1),
                () -> assertThat(매장주문_등록_검증.jsonPath().getList("orderLineItems.menu.price")).containsExactly(가격_38000.floatValue()),
                () -> assertThat(매장주문_등록_검증.jsonPath().getList("orderLineItems.menu.displayed")).containsExactly(true)
        );
    }

    private static void 주문의_종류와_상태를_검증한다(ExtractableResponse<Response> response, OrderType type, OrderStatus status) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(response.jsonPath().getString("type")).isEqualTo(type.name()),
                () -> assertThat(response.jsonPath().getString("status")).isEqualTo(status.name())
        );
    }

    private static void 매장주문의_상태가_주문완료인지_검증한다(ExtractableResponse<Response> 매장주문_완료_응답) {
        assertAll(
                () -> assertThat(매장주문_완료_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(매장주문_완료_응답.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(매장주문_완료_응답.jsonPath().getString("type")).isEqualTo(OrderType.EAT_IN.name()),
                () -> assertThat(매장주문_완료_응답.jsonPath().getString("status")).isEqualTo(OrderStatus.COMPLETED.name()),
                () -> assertThat(매장주문_완료_응답.jsonPath().getInt("orderTable.numberOfGuests")).isZero(),
                () -> assertThat(매장주문_완료_응답.jsonPath().getBoolean("orderTable.occupied")).isFalse()
        );
    }
}
