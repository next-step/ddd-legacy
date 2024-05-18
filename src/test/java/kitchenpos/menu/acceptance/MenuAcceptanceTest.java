package kitchenpos.menu.acceptance;

import kitchenpos.support.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MenuAcceptanceTest extends AcceptanceTest {

    /**
     * <pre>
     * when 메뉴를 등록하면
     * then 메뉴 목록 조회 시 등록한 메뉴를 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {

    }

    /**
     * <pre>
     * given 2개의 메뉴를 등록하고
     * when  메뉴 목록을 조회하면
     * then  등록한 2개의 메뉴를 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {

    }

    /**
     * <pre>
     * given 메뉴를 등록하고
     * when  메뉴를 숨김해제 처리를 하면
     * then  메뉴 목록 조회 시 해당 메뉴가 조회된다.
     * then  해당 메뉴는 숨김해제 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("숨김해제처리")
    void display() {

    }

    /**
     * <pre>
     * given 메뉴를 등록하고
     * when  메뉴를 숨김해제 처리를 하면
     * then  메뉴 목록 조회 시 해당 메뉴가 조회된다.
     * then  해당 메뉴는 숨김해제 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("숨김처리")
    void hide() {

    }

    /**
     * <pre>
     * given 메뉴를 등록하고
     * when  메뉴를 숨김 처리를 하면
     * then  메뉴 목록 조회 시 해당 메뉴가 조회된다.
     * then  해당 메뉴는 숨김 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("가격수정")
    void changePrice() {

    }

}
