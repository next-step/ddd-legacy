package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MenuSteps {
    private static final String ENDPOINT = "/api/menus";

    public static ExtractableResponse<Response> 메뉴_생성_요청(String name, int price, Map<String, String> menuGroup, Map<String, String>... products) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createParams(name, price, menuGroup, products))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_가격_수정_요청(ExtractableResponse<Response> createResponse, int price) {
        String id = createResponse.body().jsonPath().getString("id");
        Map<String, Object> menu = createResponse.body().jsonPath().get(".");
        menu.put("price", price + "");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().put(ENDPOINT + "/{menuId}/price", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_비공개_요청(ExtractableResponse<Response> createResponse) {
        String id = createResponse.body().jsonPath().getString("id");
        Map<String, Object> menu = createResponse.body().jsonPath().get(".");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().put(ENDPOINT + "/{menuId}/hide", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_공개_요청(ExtractableResponse<Response> createResponse) {
        String id = createResponse.body().jsonPath().getString("id");
        Map<String, Object> menu = createResponse.body().jsonPath().get(".");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().put(ENDPOINT + "/{menuId}/display", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT)
                .then().log().all().extract();
    }

    public static void 메뉴_생성_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 메뉴_가격_수정_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 메뉴_비공개_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 메뉴_공개_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 메뉴_목록_조회_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static Map<String, Object> createParams(String name, int price, Map<String, String> menuGroup, Map<String, String>[] products) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price + "");
        params.put("displayed", true);
        params.put("menuGroup", menuGroup);
        params.put("menuGroupId", menuGroup.get("id"));
        params.put("menuProducts", createMenuProducts(products));
        return params;
    }

    private static List<Map<String, Object>> createMenuProducts(Map<String, String>[] products) {
        List<Map<String, Object>> menuProducts = new ArrayList<>();

        Arrays.stream(products).forEach(p -> {
            Map<String, Object> menuProduct = new HashMap<>();
            menuProduct.put("product", p);
            menuProduct.put("productId", p.get("id"));
            menuProduct.put("quantity", "1");
            menuProducts.add(menuProduct);
        });
        return menuProducts;
    }
}
