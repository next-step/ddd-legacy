package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kitchenpos.domain.Menu;
import org.springframework.http.MediaType;

public class MenuSteps {

  public static ExtractableResponse<Response> createMenu(Menu menu) {
    return RestAssured
        .given().log().all()
        .body(menu)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/api/menus")
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> chagePrice(UUID menuId, int price) {
    Map<String, Integer> params = new HashMap<>();
    params.put("price", price);

    return RestAssured
        .given().log().all()
        .body(params)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/menus/{menuId}/price", menuId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> chageDisplay(UUID menuId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/menus/{menuId}/display", menuId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> chageDisplayHide(UUID menuId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/menus/{menuId}/hide", menuId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> getMenus() {
    return RestAssured
        .given().log().all()
        .when().get("/api/menus")
        .then().log().all().extract();
  }
}
