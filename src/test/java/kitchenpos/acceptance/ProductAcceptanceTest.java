package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.acceptance.fixture.ProductAcceptanceFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("상품 관련 기능")
public class ProductAcceptanceTest extends AcceptanceTest {

    @DisplayName("상품을 관리한다.")
    @Test
    void productManage() {

        // 상품 생성
        ExtractableResponse<Response> createResponse = createProduct();
        assertThat(createResponse.statusCode()).isEqualTo(CREATED.value());

        // 모든 상품 조회
        ExtractableResponse<Response> findALlResponse = findAll();
        assertThat(findALlResponse.statusCode()).isEqualTo(OK.value());

        String productId = (String) findALlResponse.body().jsonPath().getList("id").get(0);

        // 상품 가격 변경
        ExtractableResponse<Response> changePriceResponse = changePrice(productId);
        assertThat(changePriceResponse.statusCode()).isEqualTo(OK.value());
    }
}
