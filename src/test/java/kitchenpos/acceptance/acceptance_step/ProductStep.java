package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import kitchenpos.test_fixture.ProductTestFixture;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductStep {

    public static ExtractableResponse<Response> 상품을_등록한다(Product product) {
        return given().log().all()
                .body(product)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/products")
                .then().log().all().extract();
    }

    public static void 상품이_등록됐다(ExtractableResponse<Response> response, String expectedName, BigDecimal expectedPrice) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/api/products/" + response.body().as(Product.class).getId());
        Product 등록된_상품 = response.body().as(Product.class);
        assertThat(등록된_상품.getId()).isNotNull();
        assertThat(등록된_상품.getName()).isEqualTo(expectedName);
        assertThat(등록된_상품.getPrice()).isEqualTo(expectedPrice);
    }

    public static void 상품_등록에_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static Product 상품이_등록된_상태다() {
        Product product = ProductTestFixture.create()
                .changeId(null)
                .changeName("상품1")
                .changePrice(BigDecimal.valueOf(10000L))
                .getProduct();
        return 상품을_등록한다(product).body().as(Product.class);
    }

    public static ExtractableResponse<Response> 상품의_가격을_변경한다(UUID id, Product product) {
        return given().log().all()
                .body(product)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/products/" + id + "/price")
                .then().log().all().extract();
    }

    public static void 상품_가격이_변경됐다(ExtractableResponse<Response> response, UUID expectedId, String expectedName, BigDecimal expectedPrice) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Product 가격_변경된_상품 = response.body().as(Product.class);
        assertThat(가격_변경된_상품.getId()).isEqualTo(expectedId);
        assertThat(가격_변경된_상품.getName()).isEqualTo(expectedName);
        assertThat(가격_변경된_상품.getPrice()).isEqualTo(expectedPrice);
    }

    public static void 상품_가격변경에_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 등록된_전체_상품을_조회한다() {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/products")
                .then().log().all()
                .extract();
    }

    public static void 등록된_전체_상품_정보_조회됐다(ExtractableResponse<Response> response, int expectedProductCount) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Product> products = response.body().jsonPath().getList(".", Product.class);
        assertThat(products)
                .isNotEmpty()
                .hasSize(expectedProductCount);
        products.forEach(product -> {
            assertThat(product.getId()).isNotNull();
            assertThat(product.getName()).isNotEmpty();
            assertThat(product.getPrice()).isNotNull();
        });
    }
}
