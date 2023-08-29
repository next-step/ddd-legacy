package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.*;
import static kitchenpos.acceptance.acceptance_step.MenuGroupStep.메뉴_그룹_등록된_상태다;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuStep {
    private MenuStep() {}

    public static ExtractableResponse<Response> 메뉴를_등록한다(Menu menu) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().post("/api/menus")
                .then().log().all()
                .extract();
    }

    public static Menu 메뉴가_등록된_상태다(Product product) {
        MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
        MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                .changeProduct(product)
                .changeQuantity(1)
                .getMenuProduct();
        Menu 등록할_메뉴 = MenuTestFixture.create()
                .changeId(null)
                .changeName("메뉴1")
                .changePrice(BigDecimal.valueOf(10000))
                .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                .changeMenuGroup(등록된_메뉴_그룹)
                .getMenu();
        return 메뉴를_등록한다(등록할_메뉴).body().as(Menu.class);
    }

    public static void 상품_가격보다_메뉴_가격이_높아져서_메뉴가_숨김_상태로_변경됐다() {
        ExtractableResponse<Response> response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/menus")
                .then().log().all()
                .extract();
        List<Menu> menus = response.body().jsonPath().getList(".", Menu.class);
        Menu menu = menus.get(0);
        assertThat(menu.isDisplayed()).isFalse();
    }
}
