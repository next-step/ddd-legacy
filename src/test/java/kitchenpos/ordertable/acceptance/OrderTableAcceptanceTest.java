package kitchenpos.ordertable.acceptance;

import kitchenpos.support.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderTableAcceptanceTest extends AcceptanceTest {

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

    }

    /**
     * <pre>
     * given 테이블을 등록하고
     * given 해당 테이블에 손님이 않아
     * when  테이블에 앉은 손님 수를 변경하면
     * then  테이블 목록 조회 시 해당 테이블에 앉아 있는 손님 수는 변경한 손님 수와 동일하다.
     * </pre>
     */
    @Test
    @DisplayName("손님 수 변경")
    void changeNumberOfGuests() {

    }

}
