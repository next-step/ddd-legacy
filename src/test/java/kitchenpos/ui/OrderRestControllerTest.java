package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import kitchenpos.objectmother.*;
import kitchenpos.ui.utils.ControllerTest;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.SERVED;
import static kitchenpos.domain.OrderStatus.WAITING;
import static kitchenpos.domain.OrderType.*;
import static kitchenpos.ui.requestor.MenuGroupRequestor.메뉴그룹생성요청_메뉴그룹반환;
import static kitchenpos.ui.requestor.MenuRequestor.메뉴생성요청_메뉴반환;
import static kitchenpos.ui.requestor.OrderRequestor.*;
import static kitchenpos.ui.requestor.OrderTableRequestor.테이블생성_착석_고객수_요청테이블반환;
import static kitchenpos.ui.requestor.OrderTableRequestor.테이블생성요청_테이블반환;
import static kitchenpos.ui.requestor.ProductRequestor.상품생성요청_상품반환;
import static org.assertj.core.api.Assertions.assertThat;

class OrderRestControllerTest extends ControllerTest {

    private MenuGroup 메뉴그룹;
    private Product 상품_1;
    private Product 상품_2;
    private MenuProduct 메뉴상품_1;
    private MenuProduct 메뉴상품_2;
    private OrderTable 착석테이블;
    private OrderTable 미착석테이블;
    private Menu 메뉴_1;
    private Menu 비노출메뉴;

    @BeforeEach
    public void setUp() {
        super.setUp();
        메뉴그룹 = 메뉴그룹생성요청_메뉴그룹반환(MenuGroupMaker.make("메뉴그룹"));
        상품_1 = 상품생성요청_상품반환(ProductMaker.make("상품1", 1500L));
        상품_2 = 상품생성요청_상품반환(ProductMaker.make("상품2", 3000L));
        메뉴상품_1 = MenuProductMaker.make(상품_1, 2);
        메뉴상품_2 = MenuProductMaker.make(상품_2, 5);
        착석테이블 = 테이블생성_착석_고객수_요청테이블반환(OrderTableMaker.make("착석테이블", 4));
        미착석테이블 = 테이블생성요청_테이블반환(OrderTableMaker.make("미착석테이블"));
        메뉴_1 = 메뉴생성요청_메뉴반환(MenuMaker.make("메뉴1", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
        비노출메뉴 = 메뉴생성요청_메뉴반환(MenuMaker.makeHideMenu("비노출메뉴", 12000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
    }

    @DisplayName("매장주문생성시 요청한 데이터로 주문이 생성되야 한다.")
    @Test
    void 매장주문생성() {
        // given
        Order 매장주문 = OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(매장주문);

        // then
        매장주문생성됨(response);
    }

    @DisplayName("매장주문생성시 테이블에 착석한 손님이 아닐경우 에러를 던진다.")
    @Test
    void 매장주문생성실패_미착석() {
        // given
        Order 매장주문 = OrderMaker.makeEatin(미착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(매장주문);

        // then
        주문생성실패됨(response);
    }

    @DisplayName("주문생성시 비노출메뉴를 주문할경우 에러를 던진다.")
    @Test
    void 주문생성실패_비노출메뉴() {
        // given
        Order 매장주문 = OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(비노출메뉴, 1, 15000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(매장주문);

        // then
        주문생성실패됨(response);
    }

    @DisplayName("주문생성시 주문가격이 메뉴가격과 일치하지 않을경우 에러를 던진다.")
    @Test
    void 주문생성실패_주문가격_메뉴가격_불일치() {
        // given
        Order 매장주문 = OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 10000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(매장주문);

        // then
        주문생성실패됨(response);
    }

    @DisplayName("주문생성시 메뉴수량이 음수일경우 에러를 던진다.")
    @Test
    void 주문생성실패_수량음수() {
        // given
        Order 배달주문 = OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, -1, 15000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(배달주문);

        // then
        주문생성실패됨(response);
    }

    @DisplayName("배달주문생성시 요청한 데이터로 주문이 생성되야 한다.")
    @Test
    void 배달주문생성() {
        // given
        Order 배달주문 = OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(배달주문);

        // then
        배달주문생성됨(response);
    }

    @DisplayName("포장주문생성시 요청한 데이터로 주문이 생성되야 한다.")
    @Test
    void 포장주문생성() {
        // given
        Order 포장주문 = OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L));

        // when
        ExtractableResponse<Response> response = 주문생성요청(포장주문);

        // then
        포장주문생성됨(response);
    }

    @DisplayName("주문대기중인 주문을 수락할경우 해당주문이 수락된다.")
    @Test
    void 주문수락() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L)));

        // when
        ExtractableResponse<Response> response = 주문수락요청(주문식별번호);

        // then
        주문수락됨(response);
    }

    @DisplayName("주문대기중이 아닌 주문을 수락할경우 에러를 던진다..")
    @Test
    void 주문수락실패_대기상태아닐경우() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문수락요청(주문식별번호);

        // then
        주문수락실패됨(response);
    }

    @DisplayName("주문대기중인 배달주문을 수락할경우 주문이 수락되며 배달요청을 수행한다.")
    @Test
    void 배달주문수락() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L)));

        // when
        ExtractableResponse<Response> response = 주문수락요청(주문식별번호);

        // then
        주문수락됨(response);
    }

    @DisplayName("주문상태가 수락일경우 제공이 가능하다.")
    @Test
    void 주문제공() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문제공요청(주문식별번호);

        // then
        주문제공됨(response);
    }

    @DisplayName("배달주문인경우 주문이 제공된경우 배달을 시작할 수 있다.")
    @Test
    void 배달주문_배달시작() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);
        주문제공요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문배달시작요청(주문식별번호);

        // then
        주문배달시작됨(response);
    }

    @DisplayName("배달주문인경우 주문이 제공된경우 배달을 시작할 수 있다.")
    @Test
    void 배달주문_배달완료() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);
        주문제공요청(주문식별번호);
        주문배달시작요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문배달완료요청(주문식별번호);

        // then
        주문배달완료됨(response);
    }

    @DisplayName("매장주문인 경우 주문상태가 제공인 경우 완료할 수 있으며 테이블을 치운다.")
    @Test
    void 매장주문완료() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);
        주문제공요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문완료요청(주문식별번호);

        // then
        매장주문완료됨(response);
    }

    @DisplayName("배달주문인 경우 배달이 완료된 경우 완료할 수 있다.")
    @Test
    void 배달주문완료() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);
        주문제공요청(주문식별번호);
        주문배달시작요청(주문식별번호);
        주문배달완료요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문완료요청(주문식별번호);

        // then
        배달주문완료됨(response);
    }

    @DisplayName("포장주문인 경우 제공되었으면 완료할 수 있다.")
    @Test
    void 포장주문완료() {
        // given
        UUID 주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        주문수락요청(주문식별번호);
        주문제공요청(주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문완료요청(주문식별번호);

        // then
        포장주문완료됨(response);
    }

    @DisplayName("주문을 전체조회 할 수 있다.")
    @Test
    void 주문전체조회() {
        // given
        UUID 매장주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        UUID 배달주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15000L)));
        UUID 포장주문식별번호 = 주문생성요청_주문식별번호반환(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15000L)));

        주문수락요청(포장주문식별번호);
        주문제공요청(포장주문식별번호);

        // when
        ExtractableResponse<Response> response = 주문전체조회요청();

        // then
        주문전체조회됨(response);
    }

    private void 매장주문생성됨(ExtractableResponse<Response> response) {
        Order order = response.jsonPath().getObject("$", Order.class);
        assertThat(order.getType()).isEqualTo(EAT_IN);
        assertThat(order.getStatus()).isEqualTo(WAITING);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getOrderLineItems())
                .hasSize(1)
                .extracting(OrderLineItem::getMenu)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .usingRecursiveFieldByFieldElementComparator(
                        getRecursiveComparisonConfiguration()
                )
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
        assertThat(order.getOrderTable())
                .extracting(OrderTable::getName, OrderTable::getNumberOfGuests, OrderTable::isOccupied)
                .containsExactly(착석테이블.getName(), 착석테이블.getNumberOfGuests(), 착석테이블.isOccupied());
    }

    private void 배달주문생성됨(ExtractableResponse<Response> response) {
        Order order = response.jsonPath().getObject("$", Order.class);
        assertThat(order.getType()).isEqualTo(DELIVERY);
        assertThat(order.getStatus()).isEqualTo(WAITING);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getDeliveryAddress()).isEqualTo("넥스트타워");
        assertThat(order.getOrderLineItems())
                .hasSize(1)
                .extracting(OrderLineItem::getMenu)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .usingRecursiveFieldByFieldElementComparator(
                        getRecursiveComparisonConfiguration()
                )
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    private void 포장주문생성됨(ExtractableResponse<Response> response) {
        Order order = response.jsonPath().getObject("$", Order.class);
        assertThat(order.getType()).isEqualTo(TAKEOUT);
        assertThat(order.getStatus()).isEqualTo(WAITING);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getOrderLineItems())
                .hasSize(1)
                .extracting(OrderLineItem::getMenu)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .usingRecursiveFieldByFieldElementComparator(
                        getRecursiveComparisonConfiguration()
                )
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    private void 주문생성실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 주문수락됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("status", String.class)).isEqualTo(OrderStatus.ACCEPTED.toString());
    }

    private void 주문수락실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 주문제공됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("status", String.class)).isEqualTo(OrderStatus.SERVED.toString());
    }

    private void 주문배달시작됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("status", String.class)).isEqualTo(OrderStatus.DELIVERING.toString());
    }

    private void 주문배달완료됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("status", String.class)).isEqualTo(OrderStatus.DELIVERED.toString());
    }

    private void 매장주문완료됨(ExtractableResponse<Response> response) {
        Order order = response.jsonPath().getObject("$", Order.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.getOrderTable())
                .extracting(OrderTable::getNumberOfGuests, OrderTable::isOccupied)
                .containsExactly(0, false);
    }

    private void 배달주문완료됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("status", String.class)).isEqualTo(OrderStatus.COMPLETED.toString());
    }

    private void 포장주문완료됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("status", String.class)).isEqualTo(OrderStatus.COMPLETED.toString());
    }

    private void 주문전체조회됨(ExtractableResponse<Response> response) {
        List<Order> orders = response.jsonPath().getList("$", Order.class);
        assertThat(orders)
                .hasSize(3)
                .extracting(Order::getType, Order::getStatus)
                .containsExactly(
                        Tuple.tuple(EAT_IN, WAITING),
                        Tuple.tuple(DELIVERY, WAITING),
                        Tuple.tuple(TAKEOUT, SERVED)
                );
    }

    private RecursiveComparisonConfiguration getRecursiveComparisonConfiguration() {
        return RecursiveComparisonConfiguration
                .builder()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .build();
    }

}