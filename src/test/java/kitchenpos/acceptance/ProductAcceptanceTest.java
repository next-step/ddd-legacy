package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static kitchenpos.step.ProductStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product 인수 테스트")
public class ProductAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        menuRepository.deleteAll();
        productRepository.deleteAll();
    }

    @DisplayName("상품을 등록한다")
    @Test
    void create() throws SQLException {
        // given
        Product product = createProduct("강정치킨", 17000);

        // when
        ExtractableResponse<Response> response = requestCreateProduct(product);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("가격을 변경한다")
    @Test
    public void changePrice() {
        // given
        Product createdProduct = completeCreateProduct(createProduct("강정치킨", 17000));

        // when
        ExtractableResponse<Response> response = requestChangePrice(
                createProduct("강정치킨", 18000),
                createdProduct.getId());

        // then
        assertChangeProduct(response);
    }

    @DisplayName("모든 상품을 조회한다")
    @Test
    public void findAll() {
        // given
        Product product1 = completeCreateProduct(createProduct("닭강정", 17000));
        Product product2 = completeCreateProduct(createProduct("후라이드", 18000));
        productRepository.saveAll(Arrays.asList(product1, product2));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/products")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(new TypeRef<List<Product>>() {}).size()).isEqualTo(2);
    }

    private void assertChangeProduct(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(Product.class).getPrice()).isEqualTo(new BigDecimal(18000));
    }
}
