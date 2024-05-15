package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.config.AcceptanceTest;
import kitchenpos.acceptance.step.OrderTableAcceptanceStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;

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

        // 테이블 착석
        OrderTableAcceptanceStep.sit(oneTableId);

        // 테이블 조회
        orderTables = OrderTableAcceptanceStep.findAll();

        // 손님수 변경
        OrderTableAcceptanceStep.changeNumberOfGuests(oneTableId, createOrderTable(oneTableId, oneTableName, 4));

        // 테이블 비우기
        OrderTableAcceptanceStep.clear(oneTableId);

        // 테이블 조회
        orderTables = OrderTableAcceptanceStep.findAll();
    }
}
