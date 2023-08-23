package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.steps.MenuGroupSteps;
import kitchenpos.acceptance.steps.MenuSteps;
import kitchenpos.acceptance.steps.ProductSteps;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.fixture.MenuProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.acceptance.steps.MenuSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴")
public class MenuAcceptanceTest extends AcceptanceTest {

    private final static String NAME = "기본메뉴";
    private final static BigDecimal PRICE_1000 = BigDecimal.valueOf(1000);

    private MenuGroup menuGroup;
    private Product product_1000;
    private Product product_2000;
    private MenuProduct menuProduct_1000;
    private MenuProduct menuProduct_2000;

    @BeforeEach
    void setup() {
        menuGroup = MenuGroupSteps.메뉴그룹을_생성한다("메뉴그룹").as(MenuGroup.class);
        product_1000 = ProductSteps.상품을_생성한다("상품", BigDecimal.valueOf(1000)).as(Product.class);
        product_2000 = ProductSteps.상품을_생성한다("상품", BigDecimal.valueOf(2000)).as(Product.class);
        menuProduct_1000 = MenuProductFixture.create(product_1000, 1);
        menuProduct_2000 = MenuProductFixture.create(product_2000, 1);
    }

    @DisplayName("[성공] 메뉴 등록")
    @Test
    void createTest1() {
        //when
        ExtractableResponse<Response> response = MenuSteps.메뉴를_생성한다(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct_1000));
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME)
                , () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class))
                        .isEqualTo(PRICE_1000)
                , () -> assertThat(response.jsonPath().getList("menuProducts.product.id", UUID.class))
                        .contains(menuProduct_1000.getProductId())
                , () -> assertThat(response.jsonPath().getList("menuProducts.quantity", long.class))
                        .contains(menuProduct_1000.getQuantity())
        );
    }

    /**
     * given 메뉴를 생성한다.
     * when 메뉴 가격을 바꾼다.
     * then 메뉴 가격이 바뀐다.
     */
    @DisplayName("[성공] 메뉴 가격 수정")
    @Test
    void changePriceTest1() {
        //given
        UUID menuId = 메뉴를_생성하고_식별자를_반환한다();
        //then
        BigDecimal changePrice = BigDecimal.valueOf(900);
        ExtractableResponse<Response> response = 메뉴_가격을_수정한다(menuId, changePrice);
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class))
                        .isEqualTo(changePrice)
        );
    }


    /**
     * given 메뉴를 생성한다.
     * when 메뉴 가격을 바꾼다.
     * then 바꿀 메뉴 가격이 메뉴구성상품 1개의 가격*수량 보다 클 수 없다.
     */
    @DisplayName("[실패] 메뉴 가격 수정 실패")
    @Test
    void changePriceTest2() {
        //given
        UUID menuId = 메뉴를_생성하고_식별자를_반환한다();
        //then
        BigDecimal changePrice = BigDecimal.valueOf(1200);
        ExtractableResponse<Response> response = 메뉴_가격을_수정한다(menuId, changePrice);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }

    /**
     * given 메뉴를 생성한다. 메뉴를 숨긴다.
     * when 메뉴를 보이게 한다.
     * then 메뉴가 보인다.
     */
    @DisplayName("[성공] 메뉴 보이기")
    @Test
    void displayTest1() {
        //given
        UUID menuId = 메뉴를_생성하고_식별자를_반환한다();
        메뉴를_숨긴다(menuId);
        //then
        ExtractableResponse<Response> response = 메뉴를_노출한다(menuId);
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getBoolean("displayed"))
                        .isTrue()
        );
    }

    /**
     * given 메뉴를 생성한다.
     * when 메뉴를 숨긴다.
     * then 메뉴가 숨겨진다..
     */
    @DisplayName("[성공] 메뉴 숨기기")
    @Test
    void hideTest1() {
        //given
        UUID menuId = 메뉴를_생성하고_식별자를_반환한다();

        //then
        ExtractableResponse<Response> response = 메뉴를_숨긴다(menuId);
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getBoolean("displayed"))
                        .isFalse()
        );
    }

    @DisplayName("[성공] 메뉴 전체 조회")
    @Test
    void findAllTest1() {
        //given
        UUID firstMenuId = 메뉴를_생성하고_식별자를_반환한다();
        UUID secondMenuId = 메뉴를_생성하고_식별자를_반환한다();
        //when
        ExtractableResponse<Response> response = 메뉴_전체를_조회한다();
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(firstMenuId, secondMenuId)
        );
    }


    private UUID 메뉴를_생성하고_식별자를_반환한다() {
        Menu menu = MenuSteps.메뉴를_생성한다(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct_1000, menuProduct_2000)).as(Menu.class);
        return menu.getId();
    }
}
