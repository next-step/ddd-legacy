package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kitchenpos.domain.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSteps {

    public static ExtractableResponse<Response> 제품_등록_요청(final RequestSpecification given, final Product product) {
        return given.body(product)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/products")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static UUID 제품이_등록됨(final RequestSpecification given, final String name, final Long price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));

        return 제품_등록_요청(given, product).jsonPath().getUUID("id");
    }

    public static ExtractableResponse<Response> 제품_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/products")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 제품_가격_수정_요청(final RequestSpecification given, final UUID id, final Product product) {
        return given.body(product)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/products/{id}/price", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
