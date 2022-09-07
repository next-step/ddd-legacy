package kitchenpos.menu;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static kitchenpos.AcceptanceTestSteps.given;
import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_생성_요청;
import static kitchenpos.product.ProductSteps.상품_생성_요청;

public class MenuSteps {
    private static UUID 추천메뉴;
    private static UUID 양념치킨;
    private static UUID 후라이드치킨;
    private static List<MenuProduct> 후라이드치킨_메뉴상품;
    private static List<MenuProduct> 양념치킨_메뉴상품;
    private static List<MenuProduct> 반반치킨_메뉴상품;

    public static ExtractableResponse<Response> 메뉴_생성_요청(Menu menu) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().post("/api/menus")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴가격_변경_요청(String path, int price) {
        Map<String, String> params = new HashMap<>();
        params.put("price", price + "");

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(path + "/price")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_전시_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/display")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨김_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/hide")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴목록_조회_요청() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/menus")
                .then().log().all().extract();
    }

    public static void 메뉴그룹_메뉴상품_생성() {
        추천메뉴 = 메뉴그룹_생성_요청("추천메뉴").as(MenuGroup.class).getId();
        양념치킨 = 상품_생성_요청("양념치킨", 19000).as(Product.class).getId();
        후라이드치킨 = 상품_생성_요청("후라이드치킨", 17000).as(Product.class).getId();

        후라이드치킨_메뉴상품 = List.of(new MenuProduct(2, 후라이드치킨));
        양념치킨_메뉴상품 = List.of(new MenuProduct(2, 양념치킨));
        반반치킨_메뉴상품 = List.of(new MenuProduct(1, 후라이드치킨), new MenuProduct(1, 양념치킨));
    }

    public static Menu 후라이드_후라이드_메뉴_생성() {
        return new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, 후라이드치킨_메뉴상품, 추천메뉴);
    }

    public static Menu 양념_양념_메뉴_생성() {
        return new Menu("양념+양념", BigDecimal.valueOf(19000), true, 양념치킨_메뉴상품, 추천메뉴);
    }

    public static Menu 양념_후라이드_메뉴_생성() {
        return new Menu("양념+후라이드", BigDecimal.valueOf(20000), true, 반반치킨_메뉴상품, 추천메뉴);
    }

    public static Menu 상품이_없는_메뉴_성성() {
        return new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, null, 추천메뉴);
    }

    public static Menu 상품_개수가_음수인_메뉴_생성() {
        return new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, List.of(new MenuProduct(-1, 후라이드치킨)), 추천메뉴);
    }

    public static Menu 메뉴가격이_상품_가격_합보다_큰_메뉴_생성() {
        return new Menu("후라이드+후라이드", BigDecimal.valueOf(35000), true, List.of(new MenuProduct(2, 후라이드치킨)), 추천메뉴);
    }

    public static Menu 이름이_없는_메뉴_생성() {
        return new Menu(null, BigDecimal.valueOf(19000), true, List.of(new MenuProduct(2, 후라이드치킨)), 추천메뉴);
    }

    public static Menu 보이지_않는_메뉴_생성() {
        return new Menu("후라이드+후라이드", BigDecimal.valueOf(22000), false, List.of(new MenuProduct(2, 후라이드치킨)), 추천메뉴);
    }
}
