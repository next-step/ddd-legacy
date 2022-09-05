package kitchenpos.product;

import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.MenuSteps.메뉴_생성_요청;
import static kitchenpos.menu.MenuSteps.메뉴목록_조회_요청;
import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_생성_요청;
import static kitchenpos.product.ProductSteps.*;
import static kitchenpos.product.ProductSteps.상품목록_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상품")
class ProductAcceptanceTest extends AcceptanceTest {
    @DisplayName("상품을 생성한다.")
    @Test
    void create() {
        var 강정치킨 = 상품_생성_요청("강정치킨", 17000);

        assertAll(
                () -> assertThat(강정치킨.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(상품목록_조회_요청().jsonPath().getList("name")).containsExactly("강정치킨")
        );
    }

    @DisplayName("이름이 없는 상품을 생성할 수 없다.")
    @Test
    void createWithNullName() {
        assertThat(상품_생성_요청(null, 17000).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상품의 가격을 변경한다.")
    @Test
    void changePrice() {
        var 강정치킨 = 상품_생성_요청("강정치킨", 17000);

        var response = 상품가격_변경_요청(강정치킨.header("Location"), 15000);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(상품목록_조회_요청().jsonPath().getList("price", BigDecimal.class))
                        .containsExactly(BigDecimal.valueOf(15000.0))
        );
    }

    @DisplayName("상품의 가격을 변경할 때, 해당 상품을 포함하는 메뉴의 가격이 메뉴상품의 가격보다 작아지면 해당 메뉴는 숨겨진다.")
    @Test
    void changePriceWithMenuHide() {
        UUID 추천메뉴 = 메뉴그룹_생성_요청("추천메뉴").as(MenuGroup.class).getId();
        UUID 양념치킨 = 상품_생성_요청("양념치킨", 19000).as(Product.class).getId();
        UUID 후라이드치킨 = 상품_생성_요청("후라이드치킨", 17000).as(Product.class).getId();
        List<MenuProduct> 메뉴_상품_목록 = List.of(new MenuProduct(1, 양념치킨), new MenuProduct(1, 후라이드치킨));
        Menu 신메뉴 = new Menu("후라이드+양념", BigDecimal.valueOf(15000), true, 메뉴_상품_목록, 추천메뉴);
        메뉴_생성_요청(신메뉴);

        상품가격_변경_요청("/api/products/" + 후라이드치킨, 14999);

        assertThat(메뉴목록_조회_요청().jsonPath().getList("displayed")).containsExactly(false);
    }

    @DisplayName("상품 목록을 조회한다.")
    @Test
    void findAll() {
        상품_생성_요청("강정치킨", 17000);
        상품_생성_요청("양념치킨", 19000);
        상품_생성_요청("간장치킨", 18000);
        상품_생성_요청("후라이드치킨", 17000);

        assertThat(상품목록_조회_요청().jsonPath().getList("name"))
                .containsExactly("강정치킨", "양념치킨", "간장치킨", "후라이드치킨");
    }
}
