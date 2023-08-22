package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.steps.*;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.OrderLineItemFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문")
public class OrderAcceptanceTest extends AcceptanceTest {

    private static final String NAME = "NAME";
    private static final String DELIVER_ADDRESS = "배달주소";

    private Menu menu;
    private OrderTable orderTable;
    private OrderLineItem orderLineItem;

    @BeforeEach
    void setup() {
        MenuGroup menuGroup = MenuGroupSteps.메뉴그룹을_생성한다("메뉴그룹").as(MenuGroup.class);
        Product product = ProductSteps.상품을_생성한다("상품", BigDecimal.valueOf(1000)).as(Product.class);
        MenuProduct menuProduct = MenuProductFixture.create(product, 1);
        menu = MenuSteps.메뉴를_생성한다("메뉴", BigDecimal.valueOf(900), menuGroup.getId(), List.of(menuProduct))
                .as(Menu.class);
        MenuSteps.메뉴를_노출한다(menu.getId());

        orderLineItem = OrderLineItemFixture.create(menu, menu.getPrice(), 1);
        orderTable = OrderTableSteps.주문테이블을_생성한다("주문테이블").as(OrderTable.class);
    }

    /**
     * given 주문 테이블에 앉는다.
     * and 주문 테이블 인원수를 바꾼다.
     * when 매장 주문을 생성한다.
     * then 매장 주문은 대기상태다.
     */
    @DisplayName("[성공] 매장 주문 등록")
    @Test
    void createTest1() {
        //given
        OrderTableSteps.주문테이블을_사용한다(orderTable.getId());
        OrderTableSteps.주문테이블의_인원수를_바꾼다(orderTable.getId(), 5);

        //when
        ExtractableResponse<Response> response = OrderSteps.매장주문을_생성한다(orderTable.getId(), List.of(orderLineItem));
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("type"))
                        .isEqualTo(OrderType.EAT_IN.name())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.WAITING.name())
        );
    }


    /**
     * when 배달 주문을 생성한다.
     * then 배달 주문은 대기상태다.
     */
    @DisplayName("[성공] 배달 주문 등록")
    @Test
    void createTest2() {
        //when
        ExtractableResponse<Response> response = OrderSteps.배달주문을_생성한다(DELIVER_ADDRESS, List.of(orderLineItem));
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("type"))
                        .isEqualTo(OrderType.DELIVERY.name())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.WAITING.name())
        );
    }

    /**
     * when 포장 주문을 생성한다.
     * then 포장 주문은 대기 상태다.
     */
    @DisplayName("[성공] 포장 주문 등록")
    @Test
    void createTest3() {
        //when
        ExtractableResponse<Response> response = OrderSteps.포장주문을_생성한다(List.of(orderLineItem));
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("type"))
                        .isEqualTo(OrderType.TAKEOUT.name())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.WAITING.name())
        );
    }

    /**
     * given 주문을 생성한다.
     * when 주문을 접수한다.
     * then 주문은 접수상태다.
     */
    @DisplayName("[성공] 주문접수")
    @Test
    void acceptTest1() {
        //given
        Order order = OrderSteps.포장주문을_생성한다(List.of(orderLineItem)).as(Order.class);
        //when
        ExtractableResponse<Response> response = OrderSteps.접수한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.ACCEPTED.name())
        );
    }

    /**
     * given 주문을 생성한다.
     * and 주문을 접수한다.
     * when 주문을 서빙한다.
     * then 주문은 서빙완료 상태다.
     */
    @DisplayName("[성공] 주문 서빙")
    @Test
    void serveTest1() {
        //given
        Order order = OrderSteps.포장주문을_생성한다(List.of(orderLineItem)).as(Order.class);
        OrderSteps.접수한다(order.getId());
        //when
        ExtractableResponse<Response> response = OrderSteps.서빙한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.SERVED.name())
        );
    }

    /**
     * given 배달 주문을 생성한다.
     * and 배달 주문을 접수한다.
     * and 배달 주무을 서빙한다.
     * when 배달 요청한다.
     * then 주문 상태는 '배달중'이다.
     */
    @DisplayName("[성공] 배달주문 배달 요청")
    @Test
    void startDeliveryTest1() {
        //given
        Order order = OrderSteps.배달주문을_생성한다(DELIVER_ADDRESS, List.of(orderLineItem)).as(Order.class);
        OrderSteps.접수한다(order.getId());
        OrderSteps.서빙한다(order.getId());
        //when
        ExtractableResponse<Response> response = OrderSteps.배달을_요청한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.DELIVERING.name())
        );
    }

    /**
     * given 배달 주문을 생성한다.
     * and 배달 주문을 접수한다.
     * and 배달 주무을 서빙한다.
     * and 배달 요청한다.
     * when 배달 완료한다.
     * then 주문 상태는 '배달완료'이다.
     */
    @DisplayName("[성공] 배달주문 배달 완료")
    @Test
    void completeDeliveryTest1() {
        //given
        Order order = OrderSteps.배달주문을_생성한다(DELIVER_ADDRESS, List.of(orderLineItem)).as(Order.class);
        OrderSteps.접수한다(order.getId());
        OrderSteps.서빙한다(order.getId());
        OrderSteps.배달을_요청한다(order.getId());
        //when
        ExtractableResponse<Response> response = OrderSteps.배달을_완료한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.DELIVERED.name())
        );
    }

    /**
     * given 배달 주문을 생성한다.
     * and 배달 주문을 접수한다.
     * and 배달 주무을 서빙한다.
     * and 배달 요청한다.
     * when 주문을 완료한다.
     * then 주문 상태는 '주문완료'이다.
     */
    @DisplayName("[성공] 배달주문 완료")
    @Test
    void completeTest1() {
        //given
        Order order = OrderSteps.배달주문을_생성한다(DELIVER_ADDRESS, List.of(orderLineItem)).as(Order.class);
        OrderSteps.접수한다(order.getId());
        OrderSteps.서빙한다(order.getId());
        OrderSteps.배달을_요청한다(order.getId());
        OrderSteps.배달을_완료한다(order.getId());
        //when
        ExtractableResponse<Response> response = OrderSteps.주문을_완료한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.COMPLETED.name())
        );
    }

    /**
     * given 포장 주문을 생성한다.
     * and 포장 주문을 접수한다.
     * and 포장 주무을 서빙한다.
     * when 주문을 완료한다.
     * then 주문 상태는 '주문완료'이다.
     */
    @DisplayName("[성공] 포장주문 완료")
    @Test
    void completeTest2() {
        //given
        Order order = OrderSteps.포장주문을_생성한다( List.of(orderLineItem)).as(Order.class);
        OrderSteps.접수한다(order.getId());
        OrderSteps.서빙한다(order.getId());
        //when
        ExtractableResponse<Response> response = OrderSteps.주문을_완료한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.COMPLETED.name())
        );
    }

    /**
     * given 주문테이블에 앉는다.
     * and 주문테이블의 인원수를 바꾼다.
     * and 매장 주문을 생성한다.
     * and 매장 주문을 접수한다.
     * and 매장 주무을 서빙한다.
     * when 주문을 완료한다.
     * then 주문 상태는 '주문완료'이다.
     */
    @DisplayName("[성공] 매장주문 완료")
    @Test
    void completeTest3() {
        //given
        OrderTableSteps.주문테이블을_사용한다(orderTable.getId());
        OrderTableSteps.주문테이블의_인원수를_바꾼다(orderTable.getId(), 5);
        Order order = OrderSteps.매장주문을_생성한다(orderTable.getId(), List.of(orderLineItem)).as(Order.class);
        OrderSteps.접수한다(order.getId());
        OrderSteps.서빙한다(order.getId());
        //when
        ExtractableResponse<Response> response = OrderSteps.주문을_완료한다(order.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getString("status"))
                        .isEqualTo(OrderStatus.COMPLETED.name())
        );
    }

    @DisplayName("[성공] 주문 전체 조회")
    @Test
    void findAllTest1() {
        //given
        Order order1 = OrderSteps.포장주문을_생성한다(List.of(orderLineItem)).as(Order.class);
        Order order2 = OrderSteps.배달주문을_생성한다(DELIVER_ADDRESS, List.of(orderLineItem)).as(Order.class);
        //when
        ExtractableResponse<Response> response = OrderSteps.주문_전체를_조회한다();
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(order1.getId(), order2.getId())
        );
    }
}
