package kitchenpos.acceptance.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static kitchenpos.acceptance.fixture.AcceptanceSupport.get;
import static kitchenpos.acceptance.fixture.AcceptanceSupport.put;

public class MenuAcceptanceFixture {
    private static final String ENDPOINT = "/api/menus";

    public static ExtractableResponse<Response> createMenu() {
        MenuGroupAcceptanceFixture.createMenu();
        ExtractableResponse<Response> findAllMenu = MenuGroupAcceptanceFixture.findAll();
        String menuGroupId = (String) findAllMenu.body().jsonPath().getList("id").get(0);

        ProductAcceptanceFixture.createProduct();
        ExtractableResponse<Response> findAllProduct = ProductAcceptanceFixture.findAll();
        String productId = (String) findAllProduct.body().jsonPath().getList("id").get(0);

        Map<String, Object> productRequest = new HashMap<>();
        productRequest.put("productId", productId);
        productRequest.put("quantity", 2);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "후라이드+후라이드");
        params.put("price", 19000);
        params.put("menuGroupId", menuGroupId);
        params.put("displayed", true);
        params.put("menuProducts", Arrays.asList(productRequest));

        return AcceptanceSupport.post(params, ENDPOINT);
    }

    public static ExtractableResponse<Response> findAllMenu() {
        return get(ENDPOINT);
    }

    public static ExtractableResponse<Response> changePrice(String menuId) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", 15000);
        return put(params, ENDPOINT + "/" + menuId + "/price");
    }

    public static ExtractableResponse<Response> hideMenu(String menuId) {
        return put(ENDPOINT + "/" + menuId + "/hide");
    }

    public static ExtractableResponse<Response> displayMenu(String menuId) {
        return put(ENDPOINT + "/" + menuId + "/display");
    }
}
