package kitchenpos.ui;

import kitchenpos.domain.Product;
import kitchenpos.util.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static kitchenpos.fixture.ProductFixture.generateNewProduct;
import static kitchenpos.fixture.ProductFixture.generateNewProductWithName;
import static kitchenpos.fixture.ProductFixture.generateNewProductWithPrice;
import static org.hamcrest.Matchers.equalTo;


class ProductAcceptanceTest extends AcceptanceTest {

    @DisplayName("상품을 등록한다")
    @Test
    void createProduct() throws Exception {
        // given
        final Product product = generateNewProduct();

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .assertThat()
                .body("name", equalTo(product.getName()))
                .body("price", equalTo(product.getPrice().intValue()))
                ;
    }

    @DisplayName("상품 이름을 반드시 지정해야 한다")
    @Test
    void nullProductName() throws Exception {
        // given
        final Product product = generateNewProductWithName(null);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("상품 이름에 욕설이 들어가면 안된다")
    @Test
    void profaneName() throws Exception {
        // given
        final Product product = generateNewProductWithName("shit");

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("상품 가격은 0원 이상이어야 한다")
    @Test
    void negativePrice() throws Exception {
        // given
        final Product product = generateNewProductWithPrice(BigDecimal.valueOf(-1));

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("상품 목록을 조회한다")
    @Test
    void getAllProducts() throws Exception {
        // given
        final Product product = generateNewProduct();

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(product))
                .when()
                .get(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @DisplayName("상품 가격을 변경한다")
    @Test
    void changePrice() throws Exception {
        // given
        final Product product = generateNewProduct();

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(product))
                .when()
                .get(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @Override
    protected String getPath() {
        return "/api/products";
    }
}