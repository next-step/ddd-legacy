package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("매장 테이블 관리")
class OrderTableAcceptanceTest extends AcceptanceTest {
    private static final String ORDER_TABLE_PATH = "/api/order-tables";

    /**
     * When : 매장 테이블을 등록하고
     * Then : 매장 테이블 전체를 조회하면, 등록한 테이블이 조회가 된다.
     */
    @DisplayName("매장 테이블을 정상 등록한다.")
    @Test
    void createTable() {
        String 매장_테이블_id = 매장_테이블을_등록_한다("장미").jsonPath().getString("id");

        assertThat(매장_테이블_전체를_한다().jsonPath().getList("id"))
            .containsOnly(매장_테이블_id);
    }

    /**
     * Given : 매장 테이블을 등록하고
     * When  : 매장 테이블에 손님이 앉으면
     * Then  : 해당 매장 테이블이 사용중으로 조회 된다.
     */
    @DisplayName("매장 테이블을 사용중으로 변경한다.")
    @Test
    void sit() {
        String 매장_테이블_id = 매장_테이블을_등록_한다("장미").jsonPath().getString("id");
        매장_테이블에_손님이_앉는다(매장_테이블_id);
        assertThat(매장_테이블_전체를_한다().jsonPath().getList("occupied"))
            .containsOnly(true);
    }

    /**
     * Given : 매장 테이블을 등록하고
     * Given : 매장 테이블에 손님이 착석하고
     * Given : 매장 테이블의 손님 수를 변경하고
     * When  : 매장 테이블을 치우면
     * Then  : 해당 매장 테이블이 처음 상태로 조회 된다.
     */
    @DisplayName("매장 테이블을 치운다")
    @Test
    void clear() {
        //Given
        String 매장_테이블_id = 매장_테이블을_등록_한다("장미").jsonPath().getString("id");
        매장_테이블에_손님이_앉는다(매장_테이블_id);
        매장_테이블의_손님_수_를_변경한다(매장_테이블_id, 8);

        //When
        매장_테이블을_치운다(매장_테이블_id);

        //Then
        Map<String, Object> 매장_테이블 = 매장_테이블_전체를_한다().jsonPath().getList("", Map.class)
            .stream()
            .filter(val -> val.get("id").equals(매장_테이블_id))
            .findFirst()
            .orElseThrow();

        assertThat(매장_테이블.get("numberOfGuests")).isEqualTo(0);
        assertThat(매장_테이블.get("occupied")).isEqualTo(false);
    }

    /**
     *  Given : 매장 테이블을 등록하고
     *  Given : 매장 테이블에 손님이 앉고
     *  When  : 매장 테일블에 앉은 손님 수를 변경하면
     *  Then  : 해당 매장 테이블의 앉은 손님 수가 변경된다.
     */
    @DisplayName("매장 테이블의 손님 수를 변경 한다.")
    @Test
    void changeNumberOfGuest1() {
        String 매장_테이블_id = 매장_테이블을_등록_한다("장미").jsonPath().getString("id");
        매장_테이블에_손님이_앉는다(매장_테이블_id);
        매장_테이블의_손님_수_를_변경한다(매장_테이블_id, 8);
        assertThat(매장_테이블_전체를_한다().jsonPath().getList("numberOfGuests"))
            .containsOnly(8);
    }

    /**
     *  Given : 매장 테이블을 등록하고
     *  When  : 매장 테일블에 앉은 손님 수를 변경하면
     *  Then  :INTERNAL_SERVER_ERROR 오류가 발생한다.
     */
    @DisplayName("사용중이지 않은, 매장 테이블의 손님 수를 변경 한다.")
    @Test
    void changeNumberOfGuest2() {
        String 매장_테이블_id = 매장_테이블을_등록_한다("장미").jsonPath().getString("id");
        assertThat(매장_테이블의_손님_수_를_변경한다(매장_테이블_id, 8).response().statusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }

    private ExtractableResponse<Response> 매장_테이블의_손님_수_를_변경한다(String id, long guestNumber) {
        return RestAssured.given().log().all()
            .body(Map.of("numberOfGuests", guestNumber))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_TABLE_PATH, id, "number-of-guests"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 매장_테이블을_치운다(String id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_TABLE_PATH, id, "clear"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 매장_테이블에_손님이_앉는다(String id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_TABLE_PATH, id, "sit"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 매장_테이블을_등록_한다(String name) {
        return RestAssured.given().log().all()
            .body(Map.of("name", name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(ORDER_TABLE_PATH)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 매장_테이블_전체를_한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(ORDER_TABLE_PATH)
            .then().log().all()
            .extract();
    }
}
