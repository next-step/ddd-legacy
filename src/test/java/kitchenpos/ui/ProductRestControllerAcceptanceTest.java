package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.ui.step.ProductStep.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductRestControllerAcceptanceTest extends Acceptance {

    /**
     * 제품 생성
     * 제품 가격 변경
     * 모든 제품 조회
     */
    @DisplayName("제품을 관리 한다.")
    @Test
    void product() {
        // Arrange
        Product product = new Product();
        product.setName("제품 이름");
        product.setPrice(BigDecimal.TEN);

        // Act
        ExtractableResponse<Response> productCreateResponse = 제품_생성_요청(product);
        UUID id = productCreateResponse.jsonPath().getUUID("id");

        // Assert
        제품_생성_확인(productCreateResponse, product);

        // Arrange
        Product param = new Product();
        param.setPrice(BigDecimal.ZERO);

        // Act
        ExtractableResponse<Response> changePriceResponse = 제품_가격_변경_요청(id, param);

        // Assert
        제품_가격_변경_완료(changePriceResponse, param);

        // Arrange
        ExtractableResponse<Response> productFindAllResponse = 모든_제품_조회_요청();

        // Act
        제품_조회_요청_확인(productFindAllResponse);
    }

    private void 제품_조회_요청_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 제품_가격_변경_완료(ExtractableResponse<Response> response, Product expected) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("price")).isEqualTo(expected.getPrice().intValue())
        );
    }

    private void 제품_생성_확인(ExtractableResponse response, Product expected) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getUUID("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(expected.getName()),
                () -> assertThat(response.jsonPath().getInt("price")).isEqualTo(expected.getPrice().intValue())
        );
    }
}
