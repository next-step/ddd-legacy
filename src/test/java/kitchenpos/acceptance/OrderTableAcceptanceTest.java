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

import static kitchenpos.acceptance.steps.OrderSteps.*;
import static kitchenpos.acceptance.steps.OrderTableSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문테이블")
public class OrderTableAcceptanceTest extends AcceptanceTest {

    private static final String NAME = "주문테이블";

    private Menu menu;

    @BeforeEach
    void setup() {
        MenuGroup menuGroup = MenuGroupSteps.메뉴그룹을_생성한다("메뉴그룹").as(MenuGroup.class);
        Product product = ProductSteps.상품을_생성한다("상품", BigDecimal.valueOf(1000)).as(Product.class);
        MenuProduct menuProduct = MenuProductFixture.create(product, 1);
        menu = MenuSteps.메뉴를_생성한다(NAME, BigDecimal.valueOf(900), menuGroup.getId(), List.of(menuProduct))
                .as(Menu.class);
        MenuSteps.메뉴를_노출한다(menu.getId());
    }

    @DisplayName("[성공] 주문테이블 등록")
    @Test
    void createTest1() {
        //when
        ExtractableResponse<Response> response = 주문테이블을_생성한다(NAME);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("name"))
                        .isEqualTo(NAME)
        );
    }


    @DisplayName("[성공] 주문테이블 앉기")
    @Test
    void sitTest1() {
        //given
        UUID orderTableId = 주문테이블을_생성_후_식별자를_반환한다();
        //when
        ExtractableResponse<Response> response = 주문테이블을_사용한다(orderTableId);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getBoolean("occupied"))
                        .isTrue()
        );
    }



    @DisplayName("[성공] 주문테이블 인원수 변경")
    @Test
    void changeNumberOfGuestsTest1() {
        //given
        UUID orderTableId = 주문테이블을_생성_후_식별자를_반환한다();
        주문테이블을_사용한다(orderTableId);
        //when
        int numberOfGuests = 5;
        ExtractableResponse<Response> response = 주문테이블의_인원수를_바꾼다(orderTableId, numberOfGuests);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getInt("numberOfGuests"))
                        .isEqualTo(numberOfGuests)
        );
    }

    @DisplayName("[예외] 사용중이 아닌 테이블은 인원수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsTest2() {
        //given
        UUID orderTableId = 주문테이블을_생성_후_식별자를_반환한다();
        //when
        int numberOfGuests = 5;
        ExtractableResponse<Response> response = 주문테이블의_인원수를_바꾼다(orderTableId, numberOfGuests);
        //then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("[성공] 주문테이블 치우기")
    @Test
    void clearTest1() {
        //given
        UUID orderTableId = 주문테이블을_생성_후_식별자를_반환한다();
        주문테이블을_사용한다(orderTableId);
        주문테이블의_인원수를_바꾼다(orderTableId, 5);

        매장주문을_생성한다(orderTableId);
        접수한다(orderTableId);
        서빙한다(orderTableId);
        주문을_완료한다(orderTableId);

        //when
        ExtractableResponse<Response> response = 주문테이블을_치운다(orderTableId);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getBoolean("occupied"))
                        .isFalse()
                , () -> assertThat(response.jsonPath().getInt("numberOfGuests"))
                        .isZero()
        );
    }

    @DisplayName("[성공] 주문테이블 전체 조회")
    @Test
    void findAllTest1() {
        //given
        UUID firstOrderTableId = 주문테이블을_생성_후_식별자를_반환한다();
        UUID secondOrderTableId = 주문테이블을_생성_후_식별자를_반환한다();
        //when
        ExtractableResponse<Response> response = 주문테이블_전체를_조회한다();
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(firstOrderTableId, secondOrderTableId)
        );
    }

    private static UUID 주문테이블을_생성_후_식별자를_반환한다() {
        return 주문테이블을_생성한다(NAME).as(OrderTable.class).getId();
    }

    private void 매장주문을_생성한다(UUID orderTableId) {
        OrderLineItem orderLineItem = OrderLineItemFixture.create(menu, menu.getPrice(), 1);
        OrderSteps.매장주문을_생성한다(orderTableId, List.of(orderLineItem)).as(Order.class);
    }
}
