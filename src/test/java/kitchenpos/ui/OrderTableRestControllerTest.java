package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.ui.utils.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.objectmother.OrderTableMaker.*;
import static kitchenpos.ui.requestor.OrderTableRequestor.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTableRestControllerTest extends ControllerTest {

    @DisplayName("테이블생성 후 테이블조회시 추가된 테이블이 조회되야 한다.")
    @Test
    void 테이블생성() {
        // when
        ExtractableResponse<Response> response = 테이블생성요청(테이블_1);

        // then
        테이블생성됨(response);
    }

    @DisplayName("테이블생성 시 이름이 없을경우 에러를 던진다.")
    @Test
    void 테이블생성실패_이름없음() {
        // when
        ExtractableResponse<Response> response = 테이블생성요청(테이블_이름없음);

        // then
        테이블생성실패됨(response);
    }

    @DisplayName("테이블에 착석시 해당 테이블을 조회할경우 착석여부가 착석상태로 조회되야 한다.")
    @Test
    void 테이블착석() {
        // given
        UUID 테이블식별번호 = 테이블생성요청_테이블식별번호반환(테이블_1);

        // when
        ExtractableResponse<Response> response = 테이블착석요청(테이블식별번호);

        // then
        테이블착석됨(response);
    }

    @DisplayName("테이블에 착석시 테이블이 존재하지 않을경우 에러를 던진다.")
    @Test
    void 테이블착석실패_테이블미존재() {
        // when
        ExtractableResponse<Response> response = 테이블착석요청(UUID.randomUUID());

        // then
        테이블착석실패됨(response);
    }

    @DisplayName("테이블을 치울경우 고객 수는 0으로 변경되며 착석여부는 비착석으로 변경된다.")
    @Test
    void 테이블청소() {

    }

    @DisplayName("테이블에 착석 한 고객의 주문이 처리되지 않은 경우 테이블을 초기화 할 수 없다.")
    @Test
    void 테이블청소실패_테이블주문미처리() {

    }

    @DisplayName("착석한 테이블에 고객수를 얘기하면 변경된다.")
    @Test
    void 고객수변경() {
        // given
        UUID 테이블식별번호 = 테이블생성요청_테이블식별번호반환(테이블_1);
        테이블착석요청(테이블식별번호);

        // when
        ExtractableResponse<Response> response = 테이블고객수요청(테이블식별번호, 테이블_고객_4명);

        // then
        고객수변경됨(response);
    }

    @DisplayName("착석한 테이블에 음수에 고객수를 얘기하면 에러를 던진다.")
    @Test
    void 고객수변경실패_고객수음수() {
        // given
        UUID 테이블식별번호 = 테이블생성요청_테이블식별번호반환(테이블_1);
        테이블착석요청(테이블식별번호);

        // when
        ExtractableResponse<Response> response = 테이블고객수요청(테이블식별번호, 테이블_고객_음수);

        // then
        고객수변경실패됨(response);
    }

    @DisplayName("착석하지 않은 테이블에 고객수를 얘기하면 에러를 던진다.")
    @Test
    void 고객수변경실패_미착석테이블() {
        // given
        UUID 테이블식별번호 = 테이블생성요청_테이블식별번호반환(테이블_1);

        // when
        ExtractableResponse<Response> response = 테이블고객수요청(테이블식별번호, 테이블_고객_4명);

        // then
        고객수변경실패됨(response);
    }

    @DisplayName("테이블 전체조회시 지금까지 등록된 테이블이 전부 조회되야 한다.")
    @Test
    void 테이블전체조회() {
        // given
        테이블생성요청(테이블_1);
        테이블생성요청(테이블_2);

        // when
        ExtractableResponse<Response> response = 테이블전체조회요청();

        // then
        테이블전체조회됨(response);
    }

    private void 테이블생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 테이블생성실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 테이블착석됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("occupied", Boolean.class)).isTrue();
    }

    private void 고객수변경됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("numberOfGuests", Integer.class)).isEqualTo(테이블_고객_4명.getNumberOfGuests());
    }

    private void 테이블착석실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 고객수변경실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 테이블전체조회됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("name"))
                .hasSize(2)
                .containsExactlyInAnyOrder(테이블_1.getName(), 테이블_2.getName());
    }

}