package kitchenpos.acceptance;

import static kitchenpos.acceptance.BaseRestAssured.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import kitchenpos.domain.Menu;
import org.springframework.http.MediaType;

public class MenuSteps {

  public static ExtractableResponse<Response> createMenu(Menu menu) {
    return given()
        .body(menu)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/api/menus")
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> chagePrice(UUID menuId, int price) {
    Map<String, Integer> params = new HashMap<>();
    params.put("price", price);

    return given()
        .body(params)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/menus/{menuId}/price", menuId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> chageDisplay(UUID menuId) {
    return given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/menus/{menuId}/display", menuId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> chageDisplayHide(UUID menuId) {
    return given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/menus/{menuId}/hide", menuId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> getMenus() {
    return given()
        .when().get("/api/menus")
        .then().log().all().extract();
  }
}
