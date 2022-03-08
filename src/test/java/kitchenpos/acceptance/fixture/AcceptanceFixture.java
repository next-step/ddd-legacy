package kitchenpos.acceptance.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class AcceptanceFixture {

    public static ExtractableResponse<Response> post(Map<String, Object> params, String endpoint) {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(endpoint)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> get(String endpoint) {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(endpoint)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put(Map<String, Object> params, String endpoint) {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(endpoint)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put(String endpoint) {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(endpoint)
                .then().log().all().extract();
    }
}
