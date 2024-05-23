package kitchenpos.ordertable.acceptance;

import io.restassured.common.mapper.TypeRef;
import kitchenpos.domain.OrderTable;
import kitchenpos.support.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블_목록을_조회한다;
import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블_인원수를_변경한다;
import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블에_앉다;
import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블을_등록한다;
import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블을_정리하다;
import static kitchenpos.ordertable.fixture.OrderTableFixture.A_테이블;
import static kitchenpos.ordertable.fixture.OrderTableFixture.B_테이블;
import static kitchenpos.support.util.random.RandomNumberOfGuestsUtil.랜덤한_1명이상_6명이하_인원을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableAcceptanceTest extends AcceptanceTest {

    private static final String ORDER_TABLE_ID_KEY = "id";

    /**
     * <pre>
     * when 테이블을 등록하면
     * then 테이블 목록 조회 시 등록한 테이블을 찾을 수 있다.
     * then 등록된 테이블은 현재 비어있는 테이블이어야 한다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 등록된_테이블 = 테이블을_등록한다(A_테이블).as(OrderTable.class);

        // then
        var 테이블_아이디_목록 = 테이블_목록을_조회한다()
                .jsonPath()
                .getList(ORDER_TABLE_ID_KEY, UUID.class);
        assertThat(테이블_아이디_목록).containsExactly(등록된_테이블.getId());
    }

    /**
     * <pre>
     * given 2개의 테이블을 등록하고
     * when  테이블 목록을 조회하면
     * then  등록한 2개의 테이블을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        var 등록된_테이블_A = 테이블을_등록한다(A_테이블).as(OrderTable.class);
        var 등록된_테이블_B = 테이블을_등록한다(B_테이블).as(OrderTable.class);

        // when
        var 테이블_아이디_목록 = 테이블_목록을_조회한다()
                .jsonPath()
                .getList(ORDER_TABLE_ID_KEY, UUID.class);

        // then
        assertThat(테이블_아이디_목록).containsExactly(등록된_테이블_A.getId(), 등록된_테이블_B.getId());
    }

    /**
     * <pre>
     * given 테이블을 등록하고
     * when  손님이 테이블에 앉으면
     * then  해당 테이블은 점유 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("점유")
    void sit() {
        // given
        var targetId = 테이블을_등록한다(A_테이블).jsonPath().getUUID(ORDER_TABLE_ID_KEY);

        // when
        테이블에_앉다(targetId);

        // then
        var 테이블_목록 = 테이블_목록을_조회한다().as(new TypeRef<List<OrderTable>>() {});
        var 테이블_optional = 테이블_목록.stream().filter(orderTable -> Objects.equals(orderTable.getId(), targetId))
                .findFirst();

        assertAll(
                () -> assertThat(테이블_optional.isPresent()).isTrue(),
                () -> assertThat(테이블_optional.get().isOccupied()).isTrue()
        );
    }

    /**
     * <pre>
     * given 테이블을 등록하고
     * given 해당 테이블에 손님이 앉았다가
     * when  식사를 다 하고 나가서 정리하면
     * then  해당 테이블은 비어있으며 앉을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("정리")
    void clear() {
        // given
        var targetId = 테이블을_등록한다(A_테이블).jsonPath().getUUID(ORDER_TABLE_ID_KEY);
        테이블에_앉다(targetId);

        // when
        테이블을_정리하다(targetId);

        // then
        var 테이블_목록 = 테이블_목록을_조회한다().as(new TypeRef<List<OrderTable>>() {});
        var 테이블_optional = 테이블_목록.stream().filter(orderTable -> Objects.equals(orderTable.getId(), targetId))
                .findFirst();

        assertAll(
                () -> assertThat(테이블_optional.isPresent()).isTrue(),
                () -> assertThat(테이블_optional.get().isOccupied()).isFalse(),
                () -> assertThat(테이블_optional.get().getNumberOfGuests()).isEqualTo(0)
        );
    }

    /**
     * <pre>
     * given 테이블을 등록하고
     * given 해당 테이블에 손님이 않아
     * when  테이블에 앉은 손님의 인원수를 변경하면
     * then  테이블 목록 조회 시 해당 테이블에 앉아 있는 인원수는 변경한 인원수와 동일하다.
     * </pre>
     */
    @Test
    @DisplayName("손님 인원수 변경")
    void changeNumberOfGuests() {
        // given
        var targetId = 테이블을_등록한다(B_테이블).jsonPath().getUUID(ORDER_TABLE_ID_KEY);
        테이블에_앉다(targetId);

        // when
        var 수정할_내용 = new OrderTable();
        수정할_내용.setId(targetId);
        var 인원수 = 랜덤한_1명이상_6명이하_인원을_생성한다();
        수정할_내용.setNumberOfGuests(인원수);
        var target = 테이블_인원수를_변경한다(수정할_내용);

        // then
        var 테이블_목록 = 테이블_목록을_조회한다().as(new TypeRef<List<OrderTable>>() {});
        var 테이블_optional = 테이블_목록.stream().filter(orderTable -> Objects.equals(orderTable.getId(), targetId))
                .findFirst();

        assertAll(
                () -> assertThat(테이블_optional.isPresent()).isTrue(),
                () -> assertThat(테이블_optional.get().getNumberOfGuests()).isEqualTo(인원수)
        );
    }

}
