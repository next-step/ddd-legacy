package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.Product;

import java.util.UUID;

public class ProductSteps {
    private static final String URI = "/api/products";

    public static ExtractableResponse<Response> createProductStep(Product product) {
        return RestAssuredUtils.post(product, URI);
    }

    public static ExtractableResponse<Response> changePriceStep(Product product, UUID id) {
        return RestAssuredUtils.put(product, String.format(URI + "/%s/price", id));
    }

    public static ExtractableResponse<Response> getProductsStep() {
        return RestAssuredUtils.get(URI);
    }
}
