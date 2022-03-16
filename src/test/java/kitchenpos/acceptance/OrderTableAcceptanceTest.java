package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.acceptance.step.OrderTableSteps.*;

@DisplayName("주문 테이블 관리 기능")
class OrderTableAcceptanceTest extends AcceptanceTest {
    private static final String ORDER_TABLE_NAME = "1번";
    private static final int NUMBER_OF_GUESTS = 5;

    @DisplayName("주문 테이블을 관리한다")
    @Test
    void manageOrderTable() {
        ExtractableResponse<Response> createResponse = 주문_테이블_생성_요청(ORDER_TABLE_NAME);
        주문_테이블_생성_완료(createResponse);

        ExtractableResponse<Response> sitResponse = 주문_테이블_착석_상태_변경_요청(createResponse);
        주문_테이블_착석_상태_변경_완료(sitResponse);

        ExtractableResponse<Response> guestsResponse = 주문_테이블_인원수_변경_요청(sitResponse, NUMBER_OF_GUESTS);
        주문_테이블_인원수_변경_완료(guestsResponse);

        ExtractableResponse<Response> clearResponse = 주문_테이블_비어있음_상태_변경_요청(guestsResponse);
        주문_테이블_비어있음_상태_변경_완료(clearResponse);
    }
}
