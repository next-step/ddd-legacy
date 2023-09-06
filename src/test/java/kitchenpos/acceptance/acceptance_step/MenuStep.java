package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.*;
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

    public static Menu 메뉴가_등록된_상태다(Product product, MenuGroup menuGroup) {
        MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                .changeProduct(product)
                .changeQuantity(1)
                .getMenuProduct();
        Menu 등록할_메뉴 = MenuTestFixture.create()
                .changeId(null)
                .changeName("메뉴1")
                .changePrice(BigDecimal.valueOf(10000))
                .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                .changeMenuGroup(menuGroup)
                .getMenu();
        return 메뉴를_등록한다(등록할_메뉴).body().as(Menu.class);
    }

    public static void 메뉴_등록에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/api/menus/" + response.body().as(Menu.class).getId());
        Menu 등록된_메뉴 = response.body().as(Menu.class);
        assertThat(등록된_메뉴.getId()).isNotNull();
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

    public static Menu 메뉴가_등록되지_않은_상태다(Product product, MenuGroup menuGroup) {
        MenuProduct 등록할_메뉴_상품 = MenuProductTestFixture.create()
                .changeProduct(product)
                .changeQuantity(1)
                .getMenuProduct();
        return MenuTestFixture.create()
                .changeId(UUID.randomUUID())
                .changeName("메뉴1")
                .changePrice(BigDecimal.valueOf(10000))
                .changeMenuProducts(Collections.singletonList(등록할_메뉴_상품))
                .changeMenuGroup(menuGroup)
                .getMenu();
    }

    public static void 메뉴_가격을_입력하지_않아서_메뉴_등록에_실패헀다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_가격을_음수로_입력하여_메뉴_등록에_실패헀다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴가_포함될_메뉴_그룹이_등록된_상태가_아니라서_메뉴_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴에_포함될_상품_정보가_비어있어서_메뉴_등록에_실패헀다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴에_포함될_상품의_개수가_음수라서_메뉴_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴에_포함될_상품이_등록된_상품이_아니라서_메뉴_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_가격이_메뉴에_포함된_모든_상품의_합보다_비싸서_메뉴_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_이름을_입력하지_않아서_메뉴_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_이름을_비속어로_입력해서_메뉴_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 메뉴_가격을_변경한다(Menu menu) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().put("/api/menus/" + menu.getId() + "/price")
                .then().log().all()
                .extract();
    }

    public static void 메뉴_가격_변경에_성공했다(ExtractableResponse<Response> response, BigDecimal expectedChangePrice) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Menu 가격_변경한_메뉴 = response.body().as(Menu.class);
        assertThat(가격_변경한_메뉴.getPrice()).isEqualTo(expectedChangePrice);
    }

    public static void 변경할_가격을_입력하지_않아서_가격_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 변경할_가격을_음수로_입력해서_가격_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 변경할_메뉴가_존재하지_않아서_가격_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_가격이_메뉴에_포함된_모든_상품의_합보다_비싸서_가격_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 메뉴를_전시_상태로_변경한다(Menu menu) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/menus/" + menu.getId() + "/display")
                .then().log().all()
                .extract();
    }

    public static void 메뉴_전시_상태_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Menu 전시_상태_변경한_메뉴 = response.body().as(Menu.class);
        assertThat(전시_상태_변경한_메뉴.isDisplayed()).isTrue();
    }

    public static void 존재하지_않는_메뉴라서_전시_상태_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_가격이_메뉴에_포함된_상품_가격의_총합보다_비싸서_전시_상태_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 메뉴를_숨김_상태로_변경한다(Menu menu) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/menus/" + menu.getId() + "/hide")
                .then().log().all()
                .extract();
    }

    public static void 메뉴_숨김_상태_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Menu 숨김_상태_변경한_메뉴 = response.body().as(Menu.class);
        assertThat(숨김_상태_변경한_메뉴.isDisplayed()).isFalse();
    }

    public static void 존재하지_않는_메뉴라서_숨김_상태_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 숨김_상태의_메뉴를_주문에_포함시켜_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
