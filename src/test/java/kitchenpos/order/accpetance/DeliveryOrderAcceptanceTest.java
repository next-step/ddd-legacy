package kitchenpos.order.accpetance;

import kitchenpos.support.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeliveryOrderAcceptanceTest extends AcceptanceTest {

    /**
     * <pre>
     * when 배달주문을 등록하면
     * then 주문 목록 조회 시 등록한 주문을 찾을 수 있다.
     * then 해당 주문은 대기 상태이다.
     * then 해당 주문의 유형은 배달주문이다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {

    }

    /**
     * <pre>
     * given 2개의 주문을 등록하고
     * when  주문 목록을 조회하면
     * then  등록한 2개의 주문을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {

    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * when  점주가 주문을 수락하면
     * then  주문 목록 조회 시 해당 주문 상태는 수락 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("수락")
    void accept() {

    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * given 점주가 주문을 수락하고 조리가 완료되어
     * when  배달원에게 음식을 전달하면
     * then  주문 목록 조회 시 해당 주문 상태는 전달 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("전달")
    void serve() {

    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * given 점주가 주문을 수락하고 조리가 완료되어
     * given 배달원이 음식을 전달받아
     * when  배달원이 배달을 시작하면
     * then  주문 목록 조회 시 해당 주문 상태는 배달중 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("배달중")
    void delivering() {

    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * given 점주가 주문을 수락하고 조리가 완료되어
     * given 배달원이 음식을 전달받아
     * given 배달원이 배달을 시작한 후
     * when  배달원이 배달을 완료하면
     * then  주문 목록 조회 시 해당 주문 상태는 배달완료 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("배달완료")
    void delivered() {

    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * given 점주가 주문을 수락하고 조리가 완료되어
     * given 배달원이 음식을 전달받아
     * given 배달원이 배달을 시작한 후
     * given 배달원이 배달을 완료하여
     * when  주문이 완료되면
     * then  주문 목록 조회 시 해당 주문 상태는 완료 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("완료")
    void complete() {

    }

}
