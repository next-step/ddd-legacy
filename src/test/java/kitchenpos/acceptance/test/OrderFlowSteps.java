package kitchenpos.acceptance.test;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OrderFlowSteps {
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
    static class OrderLineItemRequest {
        private final String menuId;
        private final long quantity;
        private final int price;

        public OrderLineItemRequest(String menuId, long quantity, int price) {
            this.menuId = menuId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getMenuId() {
            return menuId;
        }
        public long getQuantity() {
            return quantity;
        }

        public int getPrice() {
            return price;
        }
    }
    static MenuProductRequest 메뉴_상품을_생성한다(String productId, long quantity) {
        return new MenuProductRequest(productId, quantity);
    }


    static OrderLineItemRequest 주문_상품을_생성한다(String menuId, long quantity, int price) {
        return new OrderLineItemRequest(menuId, quantity, price);
    }

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

    static ExtractableResponse<Response> 배송_주문을_생성한다(String deliveryAddress, List<OrderLineItemRequest> orderLineItems) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "DELIVERY");
        params.put("deliveryAddress", deliveryAddress);
        params.put("orderLineItems", orderLineItems);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/api/orders")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 주문을_수락한다(String orderId) {
        return RestAssured.given().log().all()
                .when()
                .put("/api/orders/{orderId}/accept", orderId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 주문_물품이_모두_준비됨(String orderId) {
        return RestAssured.given().log().all()
                .when()
                .put("/api/orders/{orderId}/serve", orderId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 배송을_시작한다(String orderId) {
        return RestAssured.given().log().all()
                .when()
                .put("/api/orders/{orderId}/start-delivery", orderId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 배송을_완료한다(String orderId) {
        return RestAssured.given().log().all()
                .when()
                .put("/api/orders/{orderId}/complete-delivery", orderId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 주문을_완료한다(String orderId) {
        return RestAssured.given().log().all()
                .when()
                .put("/api/orders/{orderId}/complete", orderId)
                .then().log().all()
                .extract();
    }
}
