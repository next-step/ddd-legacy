package kitchenpos.menu;

import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.MenuSteps.*;
import static kitchenpos.menu.MenuSteps.메뉴목록_조회_요청;
import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_생성_요청;
import static kitchenpos.product.ProductSteps.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴")
class MenuAcceptanceTest extends AcceptanceTest {
    private UUID 추천메뉴;
    private UUID 양념치킨;
    private UUID 후라이드치킨;
    private Menu 신메뉴;
    private Menu 양념양념;
    private Menu 양념후라이드;

    @BeforeEach
    void init() {
        추천메뉴 = 메뉴그룹_생성_요청("추천메뉴").as(MenuGroup.class).getId();
        양념치킨 = 상품_생성_요청("양념치킨", 19000).as(Product.class).getId();
        후라이드치킨 = 상품_생성_요청("후라이드치킨", 17000).as(Product.class).getId();

        List<MenuProduct> 후라이드치킨_메뉴상품 = List.of(new MenuProduct(2, 후라이드치킨));
        List<MenuProduct> 양념치킨_메뉴상품 = List.of(new MenuProduct(2, 양념치킨));
        List<MenuProduct> 반반치킨_메뉴상품 = List.of(new MenuProduct(1, 후라이드치킨), new MenuProduct(1, 양념치킨));

        신메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, 후라이드치킨_메뉴상품, 추천메뉴);
        양념양념 = new Menu("양념+양념", BigDecimal.valueOf(19000), true, 양념치킨_메뉴상품, 추천메뉴);
        양념후라이드 = new Menu("양념+후라이드", BigDecimal.valueOf(20000), true, 반반치킨_메뉴상품, 추천메뉴);
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create() {
        var 후라이드후라이드 = 메뉴_생성_요청(신메뉴);

        var 메뉴목록 = 메뉴목록_조회_요청();

        assertAll(
                () -> assertThat(후라이드후라이드.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(메뉴목록.jsonPath().getList("name")).containsExactly("후라이드+후라이드")
        );
    }

    @DisplayName("메뉴 상품이 없으면 메뉴를 생성할 수 없다.")
    @Test
    void createWithNullMenuProduct() {
        Menu 메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, null, 추천메뉴);

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴 상품의 개수가 음수인 메뉴를 생성할 수 없다.")
    @Test
    void createWithNegativeQuantity() {
        Menu 메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, List.of(new MenuProduct(-1, 후라이드치킨)), 추천메뉴);

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     *  메뉴: 간장+양념치킨, 45000원
     *
     *  메뉴 상품:
     *   - (간장치킨, 1개)
     *   - (양념치킨, 1개)
     *
     *  상품:
     *   - 간장치킨: 20000원
     *   - 양념치킨 : 22000원
     *
     *  위 경우 예외가 발생한다.
     */
    @DisplayName("메뉴의 가격이 메뉴 상품의 가격(메뉴 상품의 개수 * 상품의 가격)의 합보다 크면 안된다.")
    @Test
    void createWithHigherPriceThanMenuProduct() {
        Menu 메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(35000), true, List.of(new MenuProduct(2, 후라이드치킨)), 추천메뉴);

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("이름이 없는 메뉴는 생성할 수 없다.")
    @Test
    void createWithNullName() {
        Menu 메뉴 = new Menu(null, BigDecimal.valueOf(19000), true, List.of(new MenuProduct(2, 후라이드치킨)), 추천메뉴);

        assertThat(메뉴_생성_요청(메뉴).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴의 가격을 변경한다.")
    @Test
    void change() {
        var 후라이드후라이드 = 메뉴_생성_요청(신메뉴);

        var response = 메뉴가격_변경_요청(후라이드후라이드.header("Location"), 20000);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴목록_조회_요청().jsonPath().getList("price", BigDecimal.class))
                        .containsExactly(BigDecimal.valueOf(20000.0))
        );
    }

    @DisplayName("메뉴의 가격을 0원 미만으로 변경할 수 없다.")
    @Test
    void changeWithNegative() {
        var 후라이드후라이드 = 메뉴_생성_요청(신메뉴);

        var response = 메뉴가격_변경_요청(후라이드후라이드.header("Location"), -1);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴의 가격을 포함된 각 메뉴 상품의 가격보다 작도록 변경할 수 없다.")
    @Test
    void changeWithHigherPriceThanMenuProduct() {
        var 후라이드후라이드 = 메뉴_생성_요청(신메뉴);

        var response = 메뉴가격_변경_요청(후라이드후라이드.header("Location"), 34001);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴를 전시한다.")
    @Test
    void display() {
        Menu 메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(22000), false, List.of(new MenuProduct(2, 후라이드치킨)), 추천메뉴);
        var 후라이드후라이드 = 메뉴_생성_요청(메뉴);

        var response = 메뉴_전시_요청(후라이드후라이드.header("Location"));

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴목록_조회_요청().jsonPath().getList("displayed")).containsExactly(true)
        );
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hide() {
        var 후라이드후라이드 = 메뉴_생성_요청(신메뉴);

        var response = 메뉴_숨김_요청(후라이드후라이드.header("Location"));

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴목록_조회_요청().jsonPath().getList("displayed")).containsExactly(false)
        );
    }

    @DisplayName("메뉴 목록을 조회한다.")
    @Test
    void findAll() {
        메뉴_생성_요청(신메뉴);
        메뉴_생성_요청(양념양념);
        메뉴_생성_요청(양념후라이드);

        var 메뉴목록 = 메뉴목록_조회_요청();

        assertThat(메뉴목록.jsonPath().getList("name"))
                .containsExactly("후라이드+후라이드", "양념+양념", "양념+후라이드");
    }
}
