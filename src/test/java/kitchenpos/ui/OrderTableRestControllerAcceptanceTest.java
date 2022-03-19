package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.ui.step.OrderTableStep.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableRestControllerAcceptanceTest extends Acceptance {

    /**
     * 생성 -> 테이블 배정 -> 손님 숫자 변경 ->  초기화 -> 조회
     */
    @DisplayName("주문 테이블을 관리 한다.")
    @Test
    void orderTable() {
        // Arrange
        OrderTable orderTable = new OrderTable();
        orderTable.setName("orderTable name");

        // Act
        ExtractableResponse<Response> createResponse = 주문_테이블_생성_요청(orderTable);
        UUID id = createResponse.jsonPath().getUUID("id");

        // Assert
        주문_테이블_생성_확인(createResponse, orderTable);

        // Act
        ExtractableResponse<Response> sitResponse = 주문_테이블_배정_요청(id);

        // Assert
        주문_테이블_배정_확인(sitResponse);

        // Arrange
        OrderTable changeParam = new OrderTable();
        changeParam.setNumberOfGuests(5);

        // Act
        ExtractableResponse<Response> changeResponse = 주문_테이블_손님_인원_변경_요청(id, changeParam);

        // Assert
        테이블_인원_변경_확인(changeResponse, changeParam);

        // Act
        ExtractableResponse<Response> clearResponse = 주문_테이블_초기화_요청(id);

        //Assert
        주문_테이블_초기화_확인(clearResponse);

        // Act
        ExtractableResponse<Response> findAllResponse = 모든_주문_테이블_조회_요청();

        // Assert
        모든_주문_테이블_조회_확인(findAllResponse);
    }

    private void 모든_주문_테이블_조회_확인(ExtractableResponse<Response> findAllResponse) {
        assertThat(findAllResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 주문_테이블_초기화_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("empty")).isTrue()
        );
    }

    private void 테이블_인원_변경_확인(ExtractableResponse<Response> response, OrderTable expected) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isEqualTo(expected.getNumberOfGuests())
        );
    }

    private void 주문_테이블_배정_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("empty")).isFalse()
        );
    }

    private void 주문_테이블_생성_확인(ExtractableResponse<Response> response, OrderTable expected) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getUUID("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(expected.getName()),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("empty")).isTrue()
        );
    }
}
