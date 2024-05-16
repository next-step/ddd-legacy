package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.OrderTableAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
class OrderTableAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("주문 테이블을 생성하고 관리하고 조회할 수 있다.")
    @Test
    void orderTableCreateAndManageAndFindAll() {
        // 테이블 생성
        Response oneTable = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        UUID oneTableId = oneTable.getBody().jsonPath().getUUID("id");
        String oneTableName = oneTable.getBody().jsonPath().getString("name");

        // 테이블 조회
        Response orderTables = OrderTableAcceptanceStep.findAll();

        JsonPath responseBody = orderTables.getBody().jsonPath();

        assertThat(responseBody.getList("id")).contains(oneTableId.toString());

        // 테이블 착석
        OrderTableAcceptanceStep.sit(oneTableId);

        // 손님수 변경
        OrderTableAcceptanceStep.changeNumberOfGuests(oneTableId, createOrderTable(oneTableId, oneTableName, 4));

        // 테이블 조회
        orderTables = OrderTableAcceptanceStep.findAll();

        responseBody = orderTables.getBody().jsonPath();

        assertThat(responseBody.getList("occupied")).contains(true);
        assertThat(responseBody.getList("numberOfGuests")).contains(4);

        // 테이블 비우기
        OrderTableAcceptanceStep.clear(oneTableId);

        // 테이블 조회
        orderTables = OrderTableAcceptanceStep.findAll();
        responseBody = orderTables.getBody().jsonPath();
        assertThat(responseBody.getList("occupied")).contains(false);
    }
}
