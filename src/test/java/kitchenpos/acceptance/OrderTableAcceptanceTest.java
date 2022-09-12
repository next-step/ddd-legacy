package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static kitchenpos.acceptance.OrderTableSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("테이블 관련 기능")
public class OrderTableAcceptanceTest extends AcceptanceTest {

    private UUID 일번_테이블;

    @BeforeEach
    void setUp() {
        super.setup();

        일번_테이블 = 테이블이_등록됨("1번");
    }

    @DisplayName("테이블을 등록한다.")
    @Test
    void addOrderTable() {
        // when
        테이블_등록_요청함("2번");

        // then
        final var 테이블_목록 = 테이블_목록_조회_요청함();
        테이블이_조회됨(테이블_목록, "2번");
    }

    @DisplayName("손님이 테이블에 앉는다.")
    @Test
    void sitOrderTable() {
        // scenario
        테이블에_손님이_앉음을_요청함(일번_테이블);
        final var 테이블_목록 = 테이블_목록_조회_요청함();
        테이블에_손님이_앉음(테이블_목록, 일번_테이블);

        테이블에_앉은_손님인원을_수정_요청함(일번_테이블, 5);
        final var 재요청한_테이블_목록 = 테이블_목록_조회_요청함();
        테이블_손님인원이_조회됨(재요청한_테이블_목록, 일번_테이블, 5);
    }

    @DisplayName("손님이 테이블에서 일어난다.")
    @Test
    void clearOrderTable() {
        // given
        테이블에_손님이_앉음을_요청함(일번_테이블);
        테이블에_앉은_손님인원을_수정_요청함(일번_테이블, 5);

        // when
        손님이_테이블에서_일어남을_요청함(일번_테이블);

        // then
        final var 테이블_목록 = 테이블_목록_조회_요청함();
        테이블이_비어있음(테이블_목록, 일번_테이블);
    }

    @DisplayName("테이블 목록을 조회한다.")
    @Test
    void showOrderTables() {
        // given
        테이블이_등록됨("2번");
        테이블이_등록됨("3번");

        // when
        final var 테이블_목록 = 테이블_목록_조회_요청함();

        // then
        테이블이_조회됨(테이블_목록, "2번", "3번");
    }

    private ExtractableResponse<Response> 테이블_등록_요청함(final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);

        return 테이블_등록_요청(given(), orderTable);
    }

    private UUID 테이블이_등록됨(final String name) {
        return 테이블_등록_요청함(name).jsonPath().getUUID("id");
    }

    private ExtractableResponse<Response> 테이블에_손님이_앉음을_요청함(final UUID id) {
        return 테이블에_손님이_앉음을_요청(given(), id);
    }

    private ExtractableResponse<Response> 테이블에_앉은_손님인원을_수정_요청함(final UUID id, final int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);

        return 테이블에_앉은_손님인원을_수정_요청(given(), id, orderTable);
    }

    private ExtractableResponse<Response> 손님이_테이블에서_일어남을_요청함(final UUID id) {
        return 손님이_테이블에서_일어남을_요청(given(), id);
    }

    private ExtractableResponse<Response> 테이블_목록_조회_요청함() {
        return 테이블_목록_조회_요청(given());
    }

    private void 테이블이_조회됨(final ExtractableResponse<Response> response, final String... names) {
        assertThat(response.jsonPath().getList("name", String.class)).contains(names);
    }

    private void 테이블에_손님이_앉음(final ExtractableResponse<Response> response, final UUID id) {
        final List<OrderTable> orderTables = response.jsonPath().getList("", OrderTable.class);
        orderTables.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.isOccupied()).isTrue());
    }

    private void 테이블_손님인원이_조회됨(final ExtractableResponse<Response> response, final UUID id, final int numberOfGuests) {
        final List<OrderTable> orderTables = response.jsonPath().getList("", OrderTable.class);
        orderTables.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.getNumberOfGuests()).isEqualTo(numberOfGuests));
    }

    private void 테이블이_비어있음(final ExtractableResponse<Response> response, final UUID id) {
        final List<OrderTable> orderTables = response.jsonPath().getList("", OrderTable.class);
        orderTables.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> {
                    assertThat(it.isOccupied()).isFalse();
                    assertThat(it.getNumberOfGuests()).isEqualTo(0);
                });
    }
}
