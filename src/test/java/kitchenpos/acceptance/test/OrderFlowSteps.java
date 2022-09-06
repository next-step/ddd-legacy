package kitchenpos.acceptance.test;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OrderFlowSteps {

    static ExtractableResponse<Response> 상품을_생성한다(String name, int price) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/api/products")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 모든_상품을_조회한다() {
        return RestAssured.given().log().all()
                .when()
                .get("/api/products")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 상품_가격을_변경한다(String id, int price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put("/api/products/{id}/price", id)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 메뉴_그룹을_생성한다(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 모든_메뉴_그룹을_조회한다() {
        return RestAssured.given().log().all()
                .when()
                .get("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    static MenuProductRequest 메뉴_상품을_생성한다(String productId, int quantity) {
        return new MenuProductRequest(productId, quantity);
    }

    static class MenuProductRequest {
        private final String productId;
        private final long quantity;

        public MenuProductRequest(String productId, long quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public long getQuantity() {
            return quantity;
        }
    }

    static ExtractableResponse<Response> 메뉴를_생성한다(String name,
                                                  int price,
                                                  String menuGroupId,
                                                  boolean displayed,
                                                  List<MenuProductRequest> menuProducts) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);
        params.put("menuGroupId", menuGroupId);
        params.put("displayed", displayed);
        params.put("menuProducts", menuProducts);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/api/menus")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 모든_메뉴를_조회한다() {
        return RestAssured.given().log().all()
                .when()
                .get("/api/menus")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 메뉴_가격을_변경한다(String menuId, int price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put("/api/menus/{menuId}/price", menuId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 메뉴를_진열한다(String menuId) {
        return RestAssured.given().log().all()
                .when()
                .put("/api/menus/{menuId}/display", menuId)
                .then().log().all()
                .extract();
    }
}
