package kitchenpos.support;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

/**
 * <pre>
 * {@link RestAssured}를 통한 HTTP 통신을 도와주는 Wrapper 클래스
 * </pre>
 */
public class RestAssuredClient {

    /**
     * <pre>
     * POST HTTP 요청을 보낼 때 사용
     * </pre>
     *
     * @param path REST API PATH
     * @param requestBody 요청객체
     * @return {@link ExtractableResponse}
     */
    public static <T> ExtractableResponse<Response> post(String path, T requestBody) {
        return RestAssured
                .given()
                .log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then()
                .log().all()
                .extract();
    }

    /**
     * <pre>
     * GET HTTP 요청을 보낼 때 사용
     * </pre>
     *
     * @param path REST API PATH
     * @return {@link ExtractableResponse}
     */
    public static ExtractableResponse<Response> get(String path) {
        return RestAssured
                .given()
                .log().all()
                .when()
                .get(path)
                .then()
                .log().all()
                .extract();
    }

    /**
     * <pre>
     * PUT HTTP 요청을 보낼 때 사용
     * </pre>
     *
     * @param path REST API PATH
     * @param requestBody 요청객체
     * @return {@link ExtractableResponse}
     */
    public static <T> ExtractableResponse<Response> put(String path, T requestBody) {
        return RestAssured
                .given()
                .log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(path)
                .then()
                .log().all()
                .extract();
    }

}
