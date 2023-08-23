package kitchenpos.acceptance;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("상품 관리")
class ProductAcceptanceTest extends AcceptanceTest {

    private static final String path = "/api/products";

    /**
     * When : 상품 3개 생성하고
     * Then : 전체 상품 조회하면 등록한 상품 찾을 수 있다.
     */
    @DisplayName("상품 생성")
    @Test
    void create() {
        //when
        상품_등록_한다("돈가스", 9000);
        상품_등록_한다("김밥", 1000);
        상품_등록_한다("떡볶이", 3500);

        //then
        ExtractableResponse<Response> response = 전체_상품을_조회_한다();
        Assertions.assertThat(response.jsonPath().getList("name"))
            .containsOnly("돈가스", "김밥", "떡볶이");
        Assertions.assertThat(response.jsonPath().getList("price"))
            .containsOnly(9000f, 1000f, 3500f);
    }

    /**
     * Given : 상품 1개 등록하고
     * Given : 등록한 상품으로 메뉴를 1개 등록하고
     * When :  등록된 메뉴 가격보다 변경 할 상품가격 * 갯수가 작으면
     * Then :  메뉴는 화면에 표시 되지 않는다.
     */
    @DisplayName("상품 가격을 변경한다.")
    @Test
    void changePrice() {

    }

    /**
     * Given : 상품 1개 등록하고
     * Given : 등록한 상품으로 메뉴를 1개 등록하고
     * When :  등록된 메뉴 가격보다  <= 변경 할 상품가격 * 갯수 조건을 만족하면
     * Then :  메뉴의 화면 표시 값은 변경되지 않는다.
     */

    public ExtractableResponse<Response> 상품_등록_한다(String name, long price) {
        Map<String, Object> input = Map.of("name", name, "price", price);
        return RestAssured.given().log().all()
            .body(input)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public ExtractableResponse<Response> 전체_상품을_조회_한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }
}
