package kitchenpos.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("상품 API")
class ProductRestControllerTest extends ControllerTest {

    @DisplayName("상품 API 테스트")
    @TestFactory
    Stream<DynamicNode> product() {
        return Stream.of(
                dynamicTest("상품을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 상품_생성_요청("치킨", BigDecimal.valueOf(15_000L));
                    상품_생성됨(response);
                }),
                dynamicTest("가격이 0미만인 상품을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 상품_생성_요청("피자", BigDecimal.valueOf(-1L));
                    상품_생성_실패됨(response);
                }),
                dynamicTest("이름이 없는 상품을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 상품_생성_요청(null, BigDecimal.valueOf(15_000L));
                    상품_생성_실패됨(response);
                }),
                dynamicTest("상품 금액을 수정한다.", () -> {
                    ExtractableResponse<Response> creatResponse = 상품_생성_요청("햄버거", BigDecimal.valueOf(15_000L));
                    상품_생성됨(creatResponse);

                    ExtractableResponse<Response> updateResponse = 상품_금액_수정_요청(creatResponse.as(Product.class).getId(), BigDecimal.valueOf(20_000L));
                    상품_금액_수정됨(updateResponse);
                    상품_금액_수정_확인됨(updateResponse.as(Product.class).getPrice(), BigDecimal.valueOf(20_000L));
                }),
                dynamicTest("상품의 가격을 0 미만으로 수정한다.", () -> {
                    ExtractableResponse<Response> creatResponse = 상품_생성_요청("라면", BigDecimal.valueOf(100L));
                    상품_생성됨(creatResponse);

                    ExtractableResponse<Response> updateResponse = 상품_금액_수정_요청(creatResponse.as(Product.class).getId(), BigDecimal.valueOf(-20_000L));
                    상품_수정_실패됨(updateResponse);
                }),
                dynamicTest("상품 목록을 조회한다.", () -> {
                    ExtractableResponse<Response> response = 상품_목록_조회_요청();
                    상품_목록_응답됨(response);
                    상품_목록_확인됨(response, "치킨", "햄버거", "라면");
                })
        );
    }

    public static ExtractableResponse<Response> 상품_생성_요청(String name, BigDecimal price) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("price", price);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().post("/api/products")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 상품_금액_수정_요청(UUID productId, BigDecimal price) {
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("price", price);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().put("/api/products/{productId}/price", productId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 상품_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/products")
                .then().log().all()
                .extract();
    }

    public static void 상품_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 상품_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 상품_금액_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 상품_금액_수정_확인됨(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).isEqualTo(expected);
    }

    public static void 상품_수정_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 상품_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 상품_목록_확인됨(ExtractableResponse<Response> response, String... names) {
        List<Product> products = response.jsonPath().getList(".", Product.class);
        List<String> productNames = products
                .stream()
                .map(Product::getName)
                .collect(Collectors.toList());
        assertThat(productNames).containsExactly(names);
    }
}
