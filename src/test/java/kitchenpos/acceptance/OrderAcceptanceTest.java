package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.acceptance.MenuGroupSteps.메뉴그룹이_등록됨;
import static kitchenpos.acceptance.MenuSteps.메뉴가_등록됨;
import static kitchenpos.acceptance.MenuSteps.메뉴상품을_구성함;
import static kitchenpos.acceptance.OrderSteps.*;
import static kitchenpos.acceptance.OrderTableSteps.*;
import static kitchenpos.acceptance.ProductSteps.제품이_등록됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 관련 기능")
public class OrderAcceptanceTest extends AcceptanceTest {

    private UUID 일번_테이블;
    private UUID 후라이드_치킨_세트;

    @BeforeEach
    void setUp() {
        super.setup();

        final UUID 후라이드치킨 = 제품이_등록됨(given(), "후라이드 치킨", 15_000L);
        final UUID 콜라 = 제품이_등록됨(given(), "콜라", 2_000L);
        final UUID 세트메뉴 = 메뉴그룹이_등록됨(given(), "세트메뉴");
        MenuProduct 후라이드치킨_1개 = 메뉴상품을_구성함(후라이드치킨, 1);
        MenuProduct 콜라_1개 = 메뉴상품을_구성함(콜라, 1);
        List<MenuProduct> 후라이트치킨_콜라 = List.of(후라이드치킨_1개, 콜라_1개);
        후라이드_치킨_세트 = 메뉴가_등록됨(given(), "후라이드 치킨 세트", 17_000L, 세트메뉴, true, 후라이트치킨_콜라);

        일번_테이블 = 테이블이_등록됨(given(), "1번");
        테이블에_손님이_앉음을_요청(given(), 일번_테이블);
        테이블에_앉은_손님인원이_수정됨(given(), 일번_테이블, 5);
    }

    @DisplayName("매장에서 치킨을 식사한다.")
    @Test
    void scenario_eat_in() {
        final var 주문내역 = 주문내역을_구성함(후라이드_치킨_세트, 17_000L, 2);
        final var 주문내역_목록 = 주문내역_목록을_구성함(주문내역);

        final UUID 매장식사_주문 = 매장식사_주문이_등록됨(일번_테이블, 주문내역_목록);
        var 주문목록 = 주문_목록_조회를_요청함();
        주문이_조회됨(주문목록, 매장식사_주문);
        주문의_상태가_변경됨(주문목록, 매장식사_주문, 대기);

        주문_승인을_요청함(매장식사_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 매장식사_주문, 승인);

        주문_서빙을_요청함(매장식사_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 매장식사_주문, 서빙완료);

        주문_완료를_요청함(매장식사_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 매장식사_주문, 완료);
    }

    @DisplayName("치킨을 포장해서 가져간다.")
    @Test
    void scenario_takeout() {
        final var 주문내역 = 주문내역을_구성함(후라이드_치킨_세트, 17_000L, 2);
        final var 주문내역_목록 = 주문내역_목록을_구성함(주문내역);

        final UUID 포장_주문 = 포장_식사_주문이_등록됨(주문내역_목록);
        var 주문목록 = 주문_목록_조회를_요청함();
        주문이_조회됨(주문목록, 포장_주문);
        주문의_상태가_변경됨(주문목록, 포장_주문, 대기);

        주문_승인을_요청함(포장_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 포장_주문, 승인);

        주문_서빙을_요청함(포장_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 포장_주문, 서빙완료);

        주문_완료를_요청함(포장_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 포장_주문, 완료);
    }

    @DisplayName("치킨을 배달시켜 받는다.")
    @Test
    void scenario_delivery() {
        final var 주문내역 = 주문내역을_구성함(후라이드_치킨_세트, 17_000L, 2);
        final var 주문내역_목록 = 주문내역_목록을_구성함(주문내역);
        final String 주소지 = "서울특별시 송파구";

        final UUID 배달_주문 = 배달_주문이_등록됨(주소지, 주문내역_목록);
        var 주문목록 = 주문_목록_조회를_요청함();
        주문이_조회됨(주문목록, 배달_주문);
        주문의_상태가_변경됨(주문목록, 배달_주문, 대기);

        주문_승인을_요청함(배달_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 배달_주문, 승인);

        주문_서빙을_요청함(배달_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 배달_주문, 서빙완료);

        주문_배달을_요청함(배달_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 배달_주문, 배송중);

        주문_배달완료를_요청함(배달_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 배달_주문, 배송완료);

        주문_완료를_요청함(배달_주문);
        주문목록 = 주문_목록_조회를_요청함();
        주문의_상태가_변경됨(주문목록, 배달_주문, 완료);
    }

    @DisplayName("주문목록을 조회한다.")
    @Test
    void showOrders() {
        // given
        final var 배달주문내역 = 주문내역을_구성함(후라이드_치킨_세트, 17_000L, 2);
        final var 배달주문내역_목록 = 주문내역_목록을_구성함(배달주문내역);
        final String 주소지 = "서울특별시 송파구";
        final UUID 배달_주문 = 배달_주문이_등록됨(주소지, 배달주문내역_목록);

        final var 포장주문내역 = 주문내역을_구성함(후라이드_치킨_세트, 17_000L, 2);
        final var 포장주문내역_목록 = 주문내역_목록을_구성함(포장주문내역);
        final UUID 포장_주문 = 포장_식사_주문이_등록됨(포장주문내역_목록);

        // when
        final var 주문목록 = 주문_목록_조회를_요청함();

        // then
        주문이_조회됨(주문목록, 배달_주문, 포장_주문);
    }

    private ExtractableResponse<Response> 주문_등록을_요청함(final String type, final UUID orderTableId, final String deliveryAddress, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(orderType(type));
        if (Objects.nonNull(orderTableId)) {
            order.setOrderTableId(orderTableId);
        }
        if (Objects.nonNull(deliveryAddress)) {
            order.setDeliveryAddress(deliveryAddress);
        }
        order.setOrderLineItems(orderLineItems);

        return 주문_등록_요청(given(), order);
    }

    private OrderType orderType(String type) {
        if (type.equals(매장식사)) {
            return OrderType.EAT_IN;
        }
        if (type.equals(포장)) {
            return OrderType.TAKEOUT;
        }
        return OrderType.DELIVERY;
    }

    private UUID 매장식사_주문이_등록됨(final UUID orderTableId, final List<OrderLineItem> orderLineItems) {
        return 주문_등록을_요청함(매장식사, orderTableId, null, orderLineItems).jsonPath().getUUID("id");
    }

    private UUID 포장_식사_주문이_등록됨(final List<OrderLineItem> orderLineItems) {
        return 주문_등록을_요청함(포장, null, null, orderLineItems).jsonPath().getUUID("id");
    }

    private UUID 배달_주문이_등록됨(final String deliveryAddress, final List<OrderLineItem> orderLineItems) {
        return 주문_등록을_요청함(배달, null, deliveryAddress, orderLineItems).jsonPath().getUUID("id");
    }

    private OrderLineItem 주문내역을_구성함(final UUID menuId, final Long price, final int quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    private List<OrderLineItem> 주문내역_목록을_구성함(final OrderLineItem... orderLineItems) {
        return List.of(orderLineItems);
    }

    private ExtractableResponse<Response> 주문_목록_조회를_요청함() {
        return 주문_목록_조회_요청(given());
    }

    private ExtractableResponse<Response> 주문_승인을_요청함(final UUID id) {
        return 주문_승인_요청(given(), id);
    }

    private ExtractableResponse<Response> 주문_서빙을_요청함(final UUID id) {
        return 주문_서빙_요청(given(), id);
    }

    private ExtractableResponse<Response> 주문_배달을_요청함(final UUID id) {
        return 주문_배달_요청(given(), id);
    }

    private ExtractableResponse<Response> 주문_배달완료를_요청함(final UUID id) {
        return 주문_배달완료_요청(given(), id);
    }

    private ExtractableResponse<Response> 주문_완료를_요청함(final UUID id) {
        return 주문_완료_요청(given(), id);
    }

    private void 주문이_조회됨(final ExtractableResponse<Response> response, final UUID... ids) {
        assertThat(response.jsonPath().getList("id", UUID.class)).contains(ids);
    }

    private void 주문의_상태가_변경됨(final ExtractableResponse<Response> response, final UUID id, final String status) {
        List<Order> orders = response.jsonPath().getList("", Order.class);
        orders.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.getStatus().name()).isEqualTo(status));
    }
}
