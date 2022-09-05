package kitchenpos.order;

import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static kitchenpos.order.OrderLineItemSteps.*;
import static kitchenpos.order.OrderSteps.*;
import static kitchenpos.ordertable.OrderTableSteps.주문테이블_생성_요청;
import static kitchenpos.ordertable.OrderTableSteps.주문테이블에_앉기_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문")
class OrderAcceptanceTest extends AcceptanceTest {
    private UUID 일번테이블;
    private OrderLineItem 주문_상품_1번;
    private OrderLineItem 주문_상품_2번;
    private OrderLineItem 보이지_않는_주문_상품;
    private OrderLineItem 음수_개수_주문_상품;

    @BeforeEach
    void init() {
        메뉴그룹_메뉴_메뉴상품_생성();
        주문_상품_1번 = 후라이드_두마리_2개_주문상품_생성();
        주문_상품_2번 = 양념_두마리_1개_주문상품_생성();
        보이지_않는_주문_상품 = 보이지_않는_주문상품_생성();
        음수_개수_주문_상품 = 음수_개수_주문상품_생성();

        일번테이블 = 주문테이블_생성_요청("1번 테이블").as(OrderTable.class).getId();
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void create() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);

        주문_생성_요청(주문);

        var 주문목록 = 주문목록_조회_요청();
        assertAll(
                () -> assertThat(주문목록.jsonPath().getList(".")).hasSize(1),
                () -> assertThat(주문목록.jsonPath().getList("[0].orderLineItems")).hasSize(2)
        );
    }

    @DisplayName("주문 타입이 없으면 주문을 생성할 수 없다.")
    @Test
    void createWithNullOrderType() {
        Order 주문 = new Order(null, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);

        assertThat(주문_생성_요청(주문).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문 상품이 없으면 주문을 생성할 수 없다.")
    @Test
    void createWithNullOrderLineItems() {
        Order 주문 = new Order(OrderType.EAT_IN, null, null, 일번테이블);

        assertThat(주문_생성_요청(주문).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문 상품의 개수가 0개 미만이면 주문을 생성할 수 없다.")
    @Test
    void createWithOrderLineItemsNegativeQuantity() {
        Order 주문 = new Order(OrderType.EAT_IN, List.of(음수_개수_주문_상품), null, 일번테이블);

        assertThat(주문_생성_요청(주문).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("보이지 않는 메뉴의 주문상품을 포함하면 주문을 생성할 수 없다.")
    @Test
    void createWithDisplayedFalseMenu() {
        Order 주문 = new Order(OrderType.EAT_IN, List.of(주문_상품_1번, 보이지_않는_주문_상품), null, 일번테이블);

        assertThat(주문_생성_요청(주문).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("배달 주소가 없으면 배달 주문을 생성할 수 없다.")
    @Test
    void createWithNullDeliveryAddress() {
        Order 주문 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);

        assertThat(주문_생성_요청(주문).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("매장식사 주문을 한다.")
    @Test
    void createEatInOrder() {
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");
        주문테이블에_앉기_요청(일번테이블.header("Location"));

        Order 주문 = new Order(OrderType.EAT_IN, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블.as(OrderTable.class).getId());

        assertAll(
                () -> assertThat(주문_생성_요청(주문).statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(주문목록_조회_요청().jsonPath().getList(".")).hasSize(1),
                () -> assertThat(주문목록_조회_요청().jsonPath().getString("[0].type")).isEqualTo("EAT_IN")
        );
    }

    @DisplayName("테이블에 앉지 않으면 매장 식사를 주문할 수 없다.")
    @Test
    void createEatInOrderNotSit() {
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");

        Order 주문 = new Order(OrderType.EAT_IN, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블.as(OrderTable.class).getId());

        assertThat(주문_생성_요청(주문).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문을 접수한다.")
    @Test
    void accept() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);

        주문_수락_요청(주문_결과.header("Location"));

        assertThat(주문목록_조회_요청().jsonPath().getString("[0].status")).isEqualTo("ACCEPTED");
    }

    /**
     * example: 이미 수락한 경우 ACCEPTED 이므로 다시 수락할 수 없다.
     */
    @DisplayName("WAITING 상태가 아닌 주문은 접수할 수 없다.")
    @Test
    void acceptNotWaitingOrder() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));

        var response = 주문_수락_요청(주문_결과.header("Location"));

        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문을 서빙한다.")
    @Test
    void serve() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));

        주문_서빙_요청(주문_결과.header("Location"));

        assertThat(주문목록_조회_요청().jsonPath().getString("[0].status")).isEqualTo("SERVED");
    }

    @DisplayName("ACCEPTED가 아닌 주문은 서빙할 수 없다.")
    @Test
    void serviceNotAcceptedOrder() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);

        assertThat(주문_서빙_요청(주문_결과.header("Location")).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문의 배달을 시작한다.")
    @Test
    void startDelivery() {
        Order 주문 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        주문_서빙_요청(주문_결과.header("Location"));

        배달_시작_요청(주문_결과.header("Location"));

        assertThat(주문목록_조회_요청().jsonPath().getString("[0].status")).isEqualTo("DELIVERING");
    }

    @DisplayName("주문 타입이 DELIVERY가 아닌 주문은 배달을 시작할 수 없다.")
    @Test
    void startDeliveryNotDeliveryOrder() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        주문_서빙_요청(주문_결과.header("Location"));

        var response = 배달_시작_요청(주문_결과.header("Location"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문 상태가 SERVED가 아닌 주문은 배달을 시작할 수 없다.")
    @Test
    void startDeliveryNotServedOrder() {
        Order 주문 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        배달_시작_요청(주문_결과.header("Location"));

        var response = 배달_시작_요청(주문_결과.header("Location"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("배달을 완료한다.")
    @Test
    void completeDelivery() {
        Order 주문 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        주문_서빙_요청(주문_결과.header("Location"));
        배달_시작_요청(주문_결과.header("Location"));

        배달_완료_요청(주문_결과.header("Location"));

        assertThat(주문목록_조회_요청().jsonPath().getString("[0].status")).isEqualTo("DELIVERED");
    }

    @DisplayName("주문 상태가 DELIVERING이 아닌 주문은 배달완료 처리할 수 없다.")
    @Test
    void completeDeliveryNotDeliveringOrder() {
        Order 주문 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        주문_서빙_요청(주문_결과.header("Location"));

        var response = 배달_완료_요청(주문_결과.header("Location"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문을 완료한다.")
    @Test
    void complete() {
        Order 주문 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        주문_서빙_요청(주문_결과.header("Location"));

        주문_처리_완료_요청(주문_결과.header("Location"));

        assertThat(주문목록_조회_요청().jsonPath().getString("[0].status")).isEqualTo("COMPLETED");
    }

    @DisplayName("배달 주문일 때, 배달 완료된 상태가 아니라면 주문 완료를 할 수 없다.")
    @Test
    void completeNotDeliveredOrder() {
        Order 주문 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", 일번테이블);
        var 주문_결과 = 주문_생성_요청(주문);
        주문_수락_요청(주문_결과.header("Location"));
        주문_서빙_요청(주문_결과.header("Location"));
        배달_시작_요청(주문_결과.header("Location"));

        var response = 주문_처리_완료_요청(주문_결과.header("Location"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void findAll() {
        Order 배달 = new Order(OrderType.DELIVERY, List.of(주문_상품_1번, 주문_상품_2번), "서울특별시", null);
        Order 포장 = new Order(OrderType.TAKEOUT, List.of(주문_상품_1번, 주문_상품_2번), null, null);

        주문_생성_요청(배달);
        주문_생성_요청(포장);

        assertThat(주문목록_조회_요청().jsonPath().getList(".")).hasSize(2);
    }
}
