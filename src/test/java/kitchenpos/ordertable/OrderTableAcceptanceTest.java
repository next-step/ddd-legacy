package kitchenpos.ordertable;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.MenuSteps.메뉴_생성_요청;
import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_생성_요청;
import static kitchenpos.order.OrderSteps.*;
import static kitchenpos.ordertable.OrderTableSteps.*;
import static kitchenpos.ordertable.OrderTableSteps.주문테이블_목록_조회_요청;
import static kitchenpos.product.ProductSteps.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 테이블")
class OrderTableAcceptanceTest extends AcceptanceTest {
    private UUID 추천메뉴;
    private UUID 후라이드치킨;
    private UUID 후라이드_두마리_메뉴;
    private OrderLineItem 주문_상품_1번;

    @DisplayName("주문 테이블을 생성한다.")
    @Test
    void create() {
        주문테이블_생성_요청("1번 테이블");

        var 주문테이블_목록 = 주문테이블_목록_조회_요청();

        assertThat(주문테이블_목록.jsonPath().getList("name"))
                .containsExactly("1번 테이블");
    }

    @DisplayName("이름이 없는 주문 테이블을 생성할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithNullName(String name) {
        assertThat(주문테이블_생성_요청(name).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("테이블을 치운다.")
    @Test
    void clear() {
        메뉴_주문상품_생성_준비작업();
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");
        주문테이블에_앉기_요청(일번테이블.header("Location"));
        var 매장주문 = 주문_생성_요청(매장식사_주문(일번테이블));
        주문_수락_요청(매장주문.header("Location"));
        주문_서빙_요청(매장주문.header("Location"));
        주문_처리_완료_요청(매장주문.header("Location"));

        var response = 주문테이블_치우기_요청(일번테이블.header("Location"));

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(주문테이블_목록_조회_요청().jsonPath().getString("[0].occupied")).isEqualTo("false")
        );
    }

    @DisplayName("완료되지 않은 주문이 있는 테이블을 치울 수 없다.")
    @Test
    void clearFail() {
        메뉴_주문상품_생성_준비작업();
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");
        주문테이블에_앉기_요청(일번테이블.header("Location"));
        var 매장주문 = 주문_생성_요청(매장식사_주문(일번테이블));
        주문_수락_요청(매장주문.header("Location"));
        주문_서빙_요청(매장주문.header("Location"));

        var response = 주문테이블_치우기_요청(일번테이블.header("Location"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("테이블의 인원 수를 변경한다.")
    @Test
    void changeNumberOfGuests() {
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");
        주문테이블에_앉기_요청(일번테이블.header("Location"));

        주문테이블_인원수_변경_요청(일번테이블.header("Location"), 10);

        assertThat(주문테이블_목록_조회_요청().jsonPath().getInt("[0].numberOfGuests")).isEqualTo(10);
    }

    @DisplayName("인원 수를 음수로 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsNegative() {
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");
        주문테이블에_앉기_요청(일번테이블.header("Location"));

        var response = 주문테이블_인원수_변경_요청(일번테이블.header("Location"), -1);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("손님이 앉은 상태가 아닌 경우 인원 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsNotOccupied() {
        var 일번테이블 = 주문테이블_생성_요청("1번 테이블");

        var response = 주문테이블_인원수_변경_요청(일번테이블.header("Location"), 10);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("주문 테이블 목록을 조회한다.")
    @Test
    void findAll() {
        주문테이블_생성_요청("1번 테이블");
        주문테이블_생성_요청("2번 테이블");
        주문테이블_생성_요청("3번 테이블");

        assertThat(주문테이블_목록_조회_요청().jsonPath().getList("name"))
                .containsExactly("1번 테이블", "2번 테이블", "3번 테이블");
    }

    private void 메뉴_주문상품_생성_준비작업() {
        // 상품 생성
        추천메뉴 = 메뉴그룹_생성_요청("추천메뉴").as(MenuGroup.class).getId();
        후라이드치킨 = 상품_생성_요청("후라이드치킨", 17000).as(Product.class).getId();

        // 상품 메뉴 생성
        List<MenuProduct> 후라이드치킨_메뉴상품 = List.of(new MenuProduct(2, 후라이드치킨));

        // 메뉴 생성
        Menu 메뉴1번 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, 후라이드치킨_메뉴상품, 추천메뉴);
        후라이드_두마리_메뉴 = 메뉴_생성_요청(메뉴1번).as(Menu.class).getId();

        // 주문 상품 생성
        주문_상품_1번 = new OrderLineItem(2, 후라이드_두마리_메뉴, BigDecimal.valueOf(19000.0));
    }

    private Order 매장식사_주문(ExtractableResponse<Response> orderTable) {
        return new Order(OrderType.EAT_IN, List.of(주문_상품_1번), null, orderTable.as(OrderTable.class).getId());
    }
}
