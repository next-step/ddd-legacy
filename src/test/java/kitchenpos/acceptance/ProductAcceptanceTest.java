package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("상품 관리")
class ProductAcceptanceTest extends AcceptanceTest {

    private static final String PRODUCT_PATH = "/api/products";

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

        assertAll(
            () -> assertThat(response.jsonPath().getList("name"))
                .containsOnly("돈가스", "김밥", "떡볶이"),
            () -> assertThat(response.jsonPath().getList("price"))
                .containsOnly(9000f, 1000f, 3500f)
        );
    }

    /**
     * Given : 상품 1개 등록하고
     * When :  해당 상품의 가격을 변경하면
     * Then :  변경된 가격의 상품이 조회된다.
     */
    @DisplayName("상품 가격을 변경한다.")
    @Test
    void changePrice1() {
        //Give
        String id = 상품_등록_한다("돈가스", 9000).jsonPath().getString("id");

        //When
        상품_가격을_변경한다(id, 4500);

        //Then
        ExtractableResponse<Response> response = 전체_상품을_조회_한다();
        assertThat(response.jsonPath().getList("price"))
            .containsOnly(4500f);
    }

    /**
     * Given : 상품 1개 등록하고
     * Given : 메뉴를 등록하고
     * When  : 메뉴에 등록된 상품 가격을 아래 조건에 맞게 변경하면
     *         메뉴가격 > 상품가격 * 갯수
     * Then :  상품 가격은 변경된다.
     * Then :  메뉴의 화면에서 숨겨진다.
     */
    @DisplayName("기등록된 메뉴의 상품 가격을 변경한다.")
    @Test
    void changePrice2() {
        //Given
        String 돈가스id = 상품_등록_한다("돈가스", 9000).jsonPath().getString("id");
        메뉴_등록_한다(getMenuInput(돈가스id, 9000));

        //When
        상품_가격을_변경한다(돈가스id, 3000);

        //Then
        ExtractableResponse<Response> response = 전체_상품을_조회_한다();
        assertThat(response.jsonPath().getList("price"))
            .containsOnly(3000f);

    }

    public static ExtractableResponse<Response> 상품_등록_한다(String name, long price) {
        Map<String, Object> input = Map.of("name", name, "price", price);
        return RestAssured.given().log().all()
            .body(input)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(PRODUCT_PATH)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 상품_가격을_변경한다(String id, long price) {
        return RestAssured.given().log().all()
            .body(Map.of("price", price))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", PRODUCT_PATH, id, "price"))
            .then().log().all()
            .extract();

    }

    private ExtractableResponse<Response> 전체_상품을_조회_한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(PRODUCT_PATH)
            .then().log().all()
            .extract();
    }
}
