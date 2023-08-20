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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴관리기능")
public class MenuAcceptanceTest extends AcceptanceTest {

    private String NAME = "기본메뉴";
    private BigDecimal PRICE_1000 = BigDecimal.valueOf(1000);
    private BigDecimal PRICE_2000= BigDecimal.valueOf(2000);

    private MenuGroup menuGroup;
    private Product product;
    private Product product2;
    private MenuProduct menuProduct;
    private MenuProduct menuProduct2;

    @BeforeEach
    void setup() {
        menuGroup = MenuGroupSteps.메뉴그룹_생성("메뉴그룹").as(MenuGroup.class);
        product = ProductSteps.상품_생성("상품", BigDecimal.valueOf(1000)).as(Product.class);
        product2 = ProductSteps.상품_생성("상품", BigDecimal.valueOf(1000)).as(Product.class);
        menuProduct = MenuProductFixture.create(product, 1);
        menuProduct2 = MenuProductFixture.create(product2, 1);
    }

    @DisplayName("메뉴 등록")
    @Test
    void create() {
        //when
        ExtractableResponse<Response> response = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct));
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME)
                , () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class))
                        .isEqualTo(PRICE_1000)
                , () -> assertThat(response.jsonPath().getList("menuProducts.product.id", UUID.class))
                        .contains(menuProduct.getProductId())
                , () -> assertThat(response.jsonPath().getList("menuProducts.quantity", long.class))
                        .contains(menuProduct.getQuantity())
        );
    }

    /**
     * given 메뉴를 생성한다.
     * when 메뉴 가격을 바꾼다.
     * then 메뉴 가격이 바뀐다.
     */
    @DisplayName("[성공] 메뉴 가격 수정")
    @Test
    void change_price() {
        //given
        Menu menu = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct)).as(Menu.class);
        //then
        BigDecimal changePrice = BigDecimal.valueOf(900);
        ExtractableResponse<Response> response = MenuSteps.메뉴_가격_수정(menu.getId(), changePrice);
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
    void change_price_test_2() {
        //given
        Menu menu = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct)).as(Menu.class);
        //then
        BigDecimal changePrice = BigDecimal.valueOf(1200);
        ExtractableResponse<Response> response = MenuSteps.메뉴_가격_수정(menu.getId(), changePrice);
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
    void display_test_1() {
        //given
        Menu menu = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct, menuProduct2)).as(Menu.class);
        MenuSteps.메뉴_숨기기(menu.getId());
        //then
        ExtractableResponse<Response> response = MenuSteps.메뉴_보이기(menu.getId());
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
    void hide_test_1() {
        //given
        Menu menu = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct, menuProduct2)).as(Menu.class);

        //then
        ExtractableResponse<Response> response = MenuSteps.메뉴_숨기기(menu.getId());
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getBoolean("displayed"))
                        .isFalse()
        );
    }

    @DisplayName("[성공] 메뉴 전체 조회")
    @Test
    void findAll_test_1 (){
        //given
        Menu menu = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct, menuProduct2)).as(Menu.class);
        Menu menu2 = MenuSteps.메뉴_생성(
                NAME
                , PRICE_1000
                , menuGroup.getId()
                , List.of(menuProduct, menuProduct2)).as(Menu.class);
        //when
        ExtractableResponse<Response> response = MenuSteps.메뉴_전체_조회();
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(menu.getId(), menu2.getId())
        );
    }


}
