package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.acceptance.step.ProductSteps.*;

@DisplayName("상품 관리 기능")
class ProductAcceptanceTest extends AcceptanceTest {
    private static final String PRODUCT_NAME = "탕수육";
    private static final int PRODUCT_PRICE = 10000;

    @DisplayName("상품을 관리한다")
    @Test
    void manageProduct() {
        ExtractableResponse<Response> createResponse= 상품_생성_요청(PRODUCT_NAME, PRODUCT_PRICE);
        상품_생성_완료(createResponse);

        ExtractableResponse<Response> updateResponse = 가격_수정_요청(createResponse, 15000);
        가격_수정_완료(updateResponse);

        ExtractableResponse<Response> findResponse = 상품_목록_조회_요청();
        상품_목록_조회_완료(findResponse);
    }
}
