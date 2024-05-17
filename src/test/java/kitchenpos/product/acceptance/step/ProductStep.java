package kitchenpos.product.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import kitchenpos.support.RestAssuredClient;

import java.util.UUID;

public class ProductStep {

    private static final String PRODUCT_BASE_URL = "/api/products";

    public static ExtractableResponse<Response> 제품을_등록한다(Product request) {
        return RestAssuredClient.post(PRODUCT_BASE_URL, request);
    }

    public static ExtractableResponse<Response> 제품_목록을_조회한다() {
        return RestAssuredClient.get(PRODUCT_BASE_URL);
    }

    public static ExtractableResponse<Response> 제품_가격을_수정한다(Product request) {
        var url = String.format("%s/%s/price", PRODUCT_BASE_URL, request.getId());
        return RestAssuredClient.put(url, request);
    }

}
