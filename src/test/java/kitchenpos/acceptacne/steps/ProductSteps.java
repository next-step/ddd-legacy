package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.Product;

import java.util.UUID;

public class ProductSteps {
    private static final String URI = "/api/products";

    public static ExtractableResponse<Response> 상품을_등록한다(Product product) {
        return RestAssuredUtils.post(product, URI);
    }

    public static ExtractableResponse<Response> 상품의_가격을_수정한다(UUID id, Product product) {
        return RestAssuredUtils.put(product, String.format(URI + "/%s/price", id));
    }

    public static ExtractableResponse<Response> 상품의_목록을_보여준다() {
        return RestAssuredUtils.get(URI);
    }
}
