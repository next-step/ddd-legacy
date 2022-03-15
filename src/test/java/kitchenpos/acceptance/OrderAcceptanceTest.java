package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static kitchenpos.acceptance.step.MenuGroupSteps.메뉴_그룹_등록_요청;
import static kitchenpos.acceptance.step.MenuSteps.메뉴_생성_요청;
import static kitchenpos.acceptance.step.OrderSteps.*;
import static kitchenpos.acceptance.step.OrderTableSteps.주문_테이블_생성_요청;
import static kitchenpos.acceptance.step.OrderTableSteps.주문_테이블_착석_상태_변경_요청;
import static kitchenpos.acceptance.step.ProductSteps.상품_생성_요청;

@DisplayName("주문 관리 기능")
class OrderAcceptanceTest extends AcceptanceTest {
    private Map<String, String> 탕수육;
    private Map<String, String> 짜장면;
    private Map<String, String> 짬뽕;
    private Map<String, String> 볶음밥;

    private Map<String, String> 탕수육_세트;

    private Map<String, String> 한그릇_세트;
    private Map<String, String> 두그릇_세트;
    private Map<String, String> 세그릇_세트;

    private Map<String, String> 테이블_1번;

    @BeforeEach
    public void setUp() {
        super.setUp();
        탕수육 = 상품_생성_요청("탕수육", 10000).body().jsonPath().get(".");
        짜장면 = 상품_생성_요청("짜장면", 5000).body().jsonPath().get(".");
        짬뽕 = 상품_생성_요청("짬뽕", 6000).body().jsonPath().get(".");
        볶음밥 = 상품_생성_요청("볶음밥", 7000).body().jsonPath().get(".");

        탕수육_세트 = 메뉴_그룹_등록_요청("탕수육 세트").body().jsonPath().get(".");

        한그릇_세트 = 메뉴_생성_요청("한그릇 세트", 14000, 탕수육_세트, 탕수육, 짜장면).body().jsonPath().get(".");
        두그릇_세트 = 메뉴_생성_요청("두그릇 세트", 20000, 탕수육_세트, 탕수육, 짜장면, 짬뽕).body().jsonPath().get(".");
        세그릇_세트 = 메뉴_생성_요청("세그릇 세트", 25000, 탕수육_세트, 탕수육, 짜장면, 짬뽕, 볶음밥).body().jsonPath().get(".");

        ExtractableResponse<Response> orderTableResponse = 주문_테이블_생성_요청("1번");
        테이블_1번 = 주문_테이블_착석_상태_변경_요청(orderTableResponse).body().jsonPath().get(".");
    }

    @DisplayName("배달 주문을 관리한다")
    @Test
    void manageDeliveryOrder() {
        ExtractableResponse<Response> createResponse = 배달_주문_생성_요청(한그릇_세트, 두그릇_세트, 세그릇_세트);
        주문_생성_완료(createResponse);

        ExtractableResponse<Response> acceptResponse = 주문_수락_상태_변경_요청(createResponse);
        주문_수락_상태_변경_완료(acceptResponse);

        ExtractableResponse<Response> serveResponse = 주문_제공_상태_변경_요청(acceptResponse);
        주문_제공_상태_변경_완료(serveResponse);

        ExtractableResponse<Response> startDeliveryResponse = 주문_배달중_상태_변경_요청(serveResponse);
        주문_배달중_상태_변경_완료(startDeliveryResponse);

        ExtractableResponse<Response> completeDeliveryResponse = 주문_배달완료_상태_변경_요청(startDeliveryResponse);
        주문_배달완료_상태_변경_완료(completeDeliveryResponse);

        ExtractableResponse<Response> completeResponse = 주문_완료_상태_변경_요청(completeDeliveryResponse);
        주문_완료_상태_변경_완료(completeResponse);
    }

    @DisplayName("포장 주문을 관리한다")
    @Test
    void manageTakeoutOrder() {
        ExtractableResponse<Response> createResponse = 포장_주문_생성_요청(한그릇_세트, 두그릇_세트, 세그릇_세트);
        주문_생성_완료(createResponse);

        ExtractableResponse<Response> acceptResponse = 주문_수락_상태_변경_요청(createResponse);
        주문_수락_상태_변경_완료(acceptResponse);

        ExtractableResponse<Response> serveResponse = 주문_제공_상태_변경_요청(acceptResponse);
        주문_제공_상태_변경_완료(serveResponse);

        ExtractableResponse<Response> completeResponse = 주문_완료_상태_변경_요청(serveResponse);
        주문_완료_상태_변경_완료(completeResponse);
    }

    @DisplayName("매장 식사 주문을 관리한다")
    @Test
    void manageEatInOrder() {
        ExtractableResponse<Response> createResponse = 매장_식사_주문_생성_요청(테이블_1번, 한그릇_세트, 두그릇_세트, 세그릇_세트);
        주문_생성_완료(createResponse);

        ExtractableResponse<Response> acceptResponse = 주문_수락_상태_변경_요청(createResponse);
        주문_수락_상태_변경_완료(acceptResponse);

        ExtractableResponse<Response> serveResponse = 주문_제공_상태_변경_요청(acceptResponse);
        주문_제공_상태_변경_완료(serveResponse);

        ExtractableResponse<Response> completeResponse = 주문_완료_상태_변경_요청(serveResponse);
        주문_완료_상태_변경_완료(completeResponse);
    }
}
