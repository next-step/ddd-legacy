package kitchenpos.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kitchenpos.ui.MenuGroupRestControllerTest.메뉴_그룹_생성_요청;
import static kitchenpos.ui.ProductRestControllerTest.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("메뉴 API")
class MenuRestControllerTest extends ControllerTest {
    private MenuGroup menuGroup;
    private Product product1;
    private Product product2;
    private Menu menu;

    @DisplayName("메뉴 API 테스트")
    @TestFactory
    Stream<DynamicNode> menu() {
        return Stream.of(
                dynamicTest("메뉴을 등록한다.", () -> {
                    menuGroup = 메뉴_그룹_생성_요청("menu1").as(MenuGroup.class);
                    product1 = 상품_생성_요청("치킨", BigDecimal.valueOf(15_000L)).as(Product.class);
                    product2 = 상품_생성_요청("햄버거", BigDecimal.valueOf(15_000L)).as(Product.class);

                    ExtractableResponse<Response> response = 메뉴_생성_요청("치킨, 햄버거",
                            BigDecimal.valueOf(15_000L),
                            menuGroup.getId(), product1, product2);

                    메뉴_생성됨(response);
                    menu = response.as(Menu.class);
                }),
                dynamicTest("가격이 0미만의 메뉴을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_생성_요청("치킨", BigDecimal.valueOf(-1),
                            menuGroup.getId(), product1);

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("이름이 없는 메뉴을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_생성_요청(null, BigDecimal.valueOf(15_000L),
                            menuGroup.getId(), product1);

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("메뉴 그룹 없이 메뉴을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_생성_요청("치킨", BigDecimal.valueOf(15_000L),
                            null, product1);

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("상품 없이 메뉴을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_생성_요청("치킨", BigDecimal.valueOf(15_000L),
                            menuGroup.getId());

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("존재하지 않는 상품이 포함된 메뉴을 등록한다.", () -> {
                    Product 존재하지_않는_상품 = new Product();
                    존재하지_않는_상품.setId(UUID.randomUUID());

                    ExtractableResponse<Response> response = 메뉴_생성_요청("치킨", BigDecimal.valueOf(15_000L),
                            menuGroup.getId(), 존재하지_않는_상품);

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("상품 가격보다 비싼 메뉴을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_생성_요청("피자", BigDecimal.valueOf(18_000L),
                            menuGroup.getId(), product1);

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("메뉴의 가격을 수정한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_가격_변경_요청(menu.getId(), BigDecimal.valueOf(14_000L));


                    메뉴_목록_응답됨(response);
                }),
                dynamicTest("메뉴 가격 수정에 실패한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_가격_변경_요청(menu.getId(), BigDecimal.valueOf(-15_000L));

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("메뉴를 노출상태로 변경한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_노출_요청(menu.getId());

                    메뉴_목록_응답됨(response);
                }),
                dynamicTest("메뉴를 노출상태 변경에 실패한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_노출_요청(UUID.randomUUID());


                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("메뉴를 숨김 처리한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_숨김_요청(menu.getId());

                    메뉴_목록_응답됨(response);
                }),
                dynamicTest("메뉴 숨김처리에 실패한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_숨김_요청(UUID.randomUUID());

                    메뉴_생성_실패됨(response);
                }),
                dynamicTest("메뉴 목록을 조회한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_목록_조회_요청();

                    메뉴_목록_응답됨(response);
                    메뉴_목록_확인됨(response, "치킨, 햄버거");
                    메뉴_목록_메뉴에_메뉴_상품이_포함됨(response, product1, product2);
                })
        );
    }

    public static ExtractableResponse<Response> 메뉴_생성_요청(String name, BigDecimal price, UUID menuGroupId,
                                                         Product... products) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        request.put("price", price);
        request.put("menuGroupId", menuGroupId);
        request.put("menuProducts", toMenuProducts(products));
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menus")
                .then().log().all()
                .extract();
    }

    private static List<MenuProduct> toMenuProducts(Product... products) {
        return Arrays.stream(products)
                .map(p -> {
                    MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProductId(p.getId());
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }).collect(Collectors.toList());
    }

    public static ExtractableResponse<Response> 메뉴_가격_변경_요청(UUID menuId, BigDecimal price) {
        Map<String, Object> request = new HashMap<>();
        request.put("price", price);

        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/menus/{menuId}/price", menuId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_노출_요청(UUID menuId) {
        return RestAssured
                .given().log().all()
                .when().put("/api/menus/{menuId}/display", menuId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨김_요청(UUID menuId) {
        return RestAssured
                .given().log().all()
                .when().put("/api/menus/{menuId}/hide", menuId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/menus")
                .then().log().all()
                .extract();
    }

    public static void 메뉴_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 메뉴_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 메뉴_목록_확인됨(ExtractableResponse<Response> response, String... names) {
        List<Menu> menus = response.jsonPath().getList(".", Menu.class);

        List<String> productNames = menus.stream()
                .map(Menu::getName)
                .collect(Collectors.toList());
        assertThat(productNames).containsExactly(names);
    }

    public static void 메뉴_목록_메뉴에_메뉴_상품이_포함됨(ExtractableResponse<Response> response, Product... products) {
        List<Menu> menus = response.jsonPath().getList(".", Menu.class);

        List<UUID> actualProductIds = menus.stream()
                .flatMap(menu -> menu.getMenuProducts().stream())
                .map(menuProduct -> menuProduct.getProduct().getId())
                .collect(Collectors.toList());

        List<UUID> expectedProductIds = Arrays.stream(products)
                .map(Product::getId)
                .collect(Collectors.toList());

        assertThat(actualProductIds).containsExactlyElementsOf(expectedProductIds);
    }
}
