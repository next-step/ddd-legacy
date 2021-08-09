package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static kitchenpos.step.ProductStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product 인수 테스트")
public class ProductAcceptanceTest extends AcceptanceTest {

    @DisplayName("상품을 등록한다")
    @Test
    void create() {
        // given
        Product product = createProduct("강정치킨", 17000);

        // when
        ExtractableResponse<Response> response = createProductRequest(product);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("가격을 변경한다")
    @Test
    public void changePrice()
    {
        // given
        Product createdProduct = createProductRequested(createProduct("강정치킨", 17000));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createProduct("강정치킨", 18000))
                .when().put("/api/products/{productId}/price", createdProduct.getId())
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(Product.class).getPrice()).isEqualTo(new BigDecimal(18000));
    }

}
