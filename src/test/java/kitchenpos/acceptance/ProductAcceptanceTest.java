package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
        ExtractableResponse<Response> response = requestCreateProduct(product);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("가격을 변경한다")
    @Test
    public void changePrice()
    {
        // given
        Product createdProduct = completeChangePrice(createProduct("강정치킨", 17000));

        // when
        ExtractableResponse<Response> response = requestChangePrice(
                createProduct("강정치킨", 18000),
                createdProduct.getId());

        // then
        assertChangeProduct(response);
    }

    private void assertChangeProduct(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(Product.class).getPrice()).isEqualTo(new BigDecimal(18000));
    }
}
