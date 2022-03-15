package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static kitchenpos.acceptance.step.MenuGroupSteps.메뉴_그룹_등록_요청;
import static kitchenpos.acceptance.step.MenuSteps.*;
import static kitchenpos.acceptance.step.ProductSteps.상품_생성_요청;

@DisplayName("메뉴 관리 기능")
class MenuAcceptanceTest extends AcceptanceTest {
    private static final String MENU_NAME = "한그릇 세트";
    private static final int MENU_PRICE = 14000;

    private Map<String, String> 탕수육;
    private Map<String, String> 짜장면;

    private Map<String, String> 탕수육_세트;

    @BeforeEach
    public void setUp() {
        super.setUp();
        탕수육 = 상품_생성_요청("탕수육", 10000).body().jsonPath().get(".");
        짜장면 = 상품_생성_요청("짜장면", 5000).body().jsonPath().get(".");

        탕수육_세트 = 메뉴_그룹_등록_요청("탕수육 세트").body().jsonPath().get(".");
    }

    @DisplayName("메뉴를 관리한다")
    @Test
    void manageMenu() {
        ExtractableResponse<Response> createResponse = 메뉴_생성_요청(MENU_NAME, MENU_PRICE, 탕수육_세트, 탕수육, 짜장면);
        메뉴_생성_완료(createResponse);

        ExtractableResponse<Response> updateResponse = 메뉴_가격_수정_요청(createResponse, 13000);
        메뉴_가격_수정_완료(updateResponse);

        ExtractableResponse<Response> hideResponse = 메뉴_비공개_요청(createResponse);
        메뉴_비공개_완료(hideResponse);

        ExtractableResponse<Response> displayResponse = 메뉴_공개_요청(createResponse);
        메뉴_공개_완료(displayResponse);

        ExtractableResponse<Response> findResponse = 메뉴_목록_조회_요청();
        메뉴_목록_조회_완료(findResponse);
    }
}
