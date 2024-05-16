package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.OrderTableAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import kitchenpos.domain.OrderTable;
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
        final OrderTable orderTable = createOrderTable("1번테이블");
        final Response oneTable = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        final UUID oneTableId = oneTable.getBody().jsonPath().getUUID("id");
        orderTable.setId(oneTableId);

        // 테이블 조회
        Response orderTables = OrderTableAcceptanceStep.findAll();

        assertThat(orderTables.getBody().jsonPath().getList("id", UUID.class)).contains(oneTableId);

        // 테이블 착석
        OrderTableAcceptanceStep.sit(oneTableId);

        // 손님수 변경
        orderTable.setNumberOfGuests(4);
        OrderTableAcceptanceStep.changeNumberOfGuests(oneTableId, orderTable);

        // 테이블 조회
        orderTables = OrderTableAcceptanceStep.findAll();

        assertThat(orderTables.getBody().jsonPath().getList("id", UUID.class)).contains(oneTableId);
        assertThat(orderTables.getBody().jsonPath().getList("occupied")).contains(true);
        assertThat(orderTables.getBody().jsonPath().getList("numberOfGuests")).contains(4);

        // 테이블 비우기
        OrderTableAcceptanceStep.clear(oneTableId);

        // 테이블 조회
        orderTables = OrderTableAcceptanceStep.findAll();

        assertThat(orderTables.getBody().jsonPath().getList("id", UUID.class)).contains(oneTableId);
        assertThat(orderTables.getBody().jsonPath().getList("occupied")).contains(false);
    }
}
