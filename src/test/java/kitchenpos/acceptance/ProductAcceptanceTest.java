package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("상품")
public class ProductAcceptanceTest extends AcceptanceTest {

  @DisplayName("상품을 등록한다.")
  @Test
  void createProductTest() {
    ExtractableResponse<Response> response = ProductSteps.createProduct("강정치킨", 17000);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> listResponse = ProductSteps.getProducts();

    assertThat(listResponse.jsonPath().getList("name")).contains("강정치킨");
  }

  @DisplayName("상품 이름 null 등록 에러")
  @Test
  void createProductNameNull() {
    ExtractableResponse<Response> response = ProductSteps.createProduct(null, 17000);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("상품 가격 음수 등록 에러")
  @Test
  void createProductPriceNegative() {
    ExtractableResponse<Response> response = ProductSteps.createProduct("강정치킨", -1);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("상품 가격을 수정한다.")
  @Test
  void updatePrice() {
    ExtractableResponse<Response> response = ProductSteps.createProduct("강정치킨", 17000);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ProductSteps.updateProductPrice(response.jsonPath().getUUID("id"), 18000);
    ExtractableResponse<Response> result = ProductSteps.getProducts();

    assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.jsonPath().getList("price")).contains(18000F);
  }

  @DisplayName("상품 가격 음수 등록 에러")
  @Test
  void updatePriceNegative() {
    ExtractableResponse<Response> response = ProductSteps.createProduct("강정치킨", 17000);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = ProductSteps.updateProductPrice(response.jsonPath().getUUID("id"), -1);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}