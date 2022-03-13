package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.ui.fixture.OrderTableAcceptanceFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("주문 테이블 관련 기능")
public class OrderTableAcceptanceTest extends AcceptanceTest {
    @DisplayName("주문 테이블을 관리한다.")
    @Test
    void orderTableManage() {
        // 주문 테이블을 생성 한다.
        ExtractableResponse<Response> createResponse = createOrderTable();
        assertThat(createResponse.statusCode()).isEqualTo(CREATED.value());

        // 주문 테이블들을 조회 한다.
        ExtractableResponse<Response> findAllResponse = findAll();
        assertThat(findAllResponse.statusCode()).isEqualTo(OK.value());

        String orderTableId = (String) findAllResponse.body().jsonPath().getList("id").get(0);

        // 주문 테이블을 사용 한다.
        ExtractableResponse<Response> sitResponse = sit(orderTableId);
        assertThat(sitResponse.statusCode()).isEqualTo(OK.value());

        // 주문 테이블의 인원 수를 변경할 수 있다.
        ExtractableResponse<Response> guestsResponse = changeNumberOfGuests(orderTableId);
        assertThat(guestsResponse.statusCode()).isEqualTo(OK.value());

        // 주문 테이블을 초기화 한다.
        ExtractableResponse<Response> clearResponse = clear(orderTableId);
        assertThat(clearResponse.statusCode()).isEqualTo(OK.value());
    }
}
