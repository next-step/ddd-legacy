package kitchenpos.product.acceptance;

import kitchenpos.support.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.product.acceptance.step.ProductStep.제품_목록을_조회한다;
import static kitchenpos.product.acceptance.step.ProductStep.제품을_등록한다;
import static kitchenpos.product.fixture.ProductFixture.A_제품;
import static kitchenpos.product.fixture.ProductFixture.B_제품;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductAcceptanceTest extends AcceptanceTest {

    private static final String PRODUCT_NAME_KEY = "name";

    /**
     * when 제품을 등록하면
     * then 제품 목록 조회 시 등록한 제품을 찾을 수 있다.
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        제품을_등록한다(A_제품);

        // then
        var 제품_이름_목록 = 제품_목록을_조회한다()
                .jsonPath()
                .getList(PRODUCT_NAME_KEY, String.class);
        assertThat(제품_이름_목록).containsExactly(A_제품.getName());
    }

    /**
     * given 2개의 제품을 등록하고
     * when 제품 목록을 조회하면
     * then 등록한 2개의 제품을 찾을 수 있다.
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        제품을_등록한다(A_제품);
        제품을_등록한다(B_제품);

        // when
        var 제품_이름_목록 = 제품_목록을_조회한다()
                .jsonPath()
                .getList(PRODUCT_NAME_KEY, String.class);

        // then
        assertThat(제품_이름_목록).containsExactly(A_제품.getName(), B_제품.getName());
    }

    @Test
    @DisplayName("가격수정")
    void updatePrice() {
        
    }

}
