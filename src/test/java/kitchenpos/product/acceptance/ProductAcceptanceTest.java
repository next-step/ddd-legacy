package kitchenpos.product.acceptance;

import io.restassured.common.mapper.TypeRef;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.support.AcceptanceTest;
import kitchenpos.support.AssertUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.menu.acceptance.step.MenuStep.메뉴_목록을_조회한다;
import static kitchenpos.menu.acceptance.step.MenuStep.메뉴를_등록한다;
import static kitchenpos.menu.fixture.MenuFixture.메뉴를_생성한다;
import static kitchenpos.menu.fixture.MenuFixture.제품_가격합계보다_낮은_금액을_생성한다;
import static kitchenpos.menu.fixture.MenuFixture.제품을_생성한다;
import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹을_등록한다;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.A_메뉴그룹;
import static kitchenpos.product.acceptance.step.ProductStep.제품_가격을_수정한다;
import static kitchenpos.product.acceptance.step.ProductStep.제품_목록을_조회한다;
import static kitchenpos.product.acceptance.step.ProductStep.제품을_등록한다;
import static kitchenpos.product.fixture.ProductFixture.A_제품;
import static kitchenpos.product.fixture.ProductFixture.B_제품;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductAcceptanceTest extends AcceptanceTest {

    private static final String PRODUCT_ID_KEY = "id";

    /**
     * <pre>
     * when 제품을 등록하면
     * then 제품 목록 조회 시 등록한 제품을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 등록된_제품 = 제품을_등록한다(A_제품).as(Product.class);

        // then
        var 제품_아이디_목록 = 제품_목록을_조회한다()
                .jsonPath()
                .getList(PRODUCT_ID_KEY, UUID.class);
        assertThat(제품_아이디_목록).containsExactly(등록된_제품.getId());
    }

    /**
     * <pre>
     * given 2개의 제품을 등록하고
     * when  제품 목록을 조회하면
     * then  등록한 2개의 제품을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        var 등록된_제품_A = 제품을_등록한다(A_제품).as(Product.class);
        var 등록된_제품_B = 제품을_등록한다(B_제품).as(Product.class);

        // when
        var 제품_아이디_목록 = 제품_목록을_조회한다()
                .jsonPath()
                .getList(PRODUCT_ID_KEY, UUID.class);

        // then
        assertThat(제품_아이디_목록).containsExactly(등록된_제품_A.getId(), 등록된_제품_B.getId());
    }

    @Nested
    class 가격수정 {

        /**
         * <pre>
         * given 제품을 등록하고
         * when  등록한 제품의 가격을 10,000원 인상하면
         * then  제품 목록 조회 시 해당 제품이 조회된다.
         * then  제품의 가격은 10,000원 인상된 가격이다.
         * </pre>
         */
        @Test
        @DisplayName("메뉴에 포함되지 않은 제품")
        void standalone() {
            // given
            var target = 제품을_등록한다(B_제품).as(Product.class);

            // when
            var 수정할_내용 = new Product();
            수정할_내용.setId(target.getId());
            수정할_내용.setPrice(target.getPrice().add(new BigDecimal(10000)));
            제품_가격을_수정한다(수정할_내용);

            // then
            var 제품_목록 = 제품_목록을_조회한다().as(new TypeRef<List<Product>>() {});
            var 제품_optional = 제품_목록.stream().filter(product -> Objects.equals(product.getId(), target.getId()))
                    .findFirst();

            assertAll(
                    () -> assertThat(제품_optional.isPresent()).isTrue(),
                    () -> AssertUtils.가격이_동등한가(제품_optional.get().getPrice(), 수정할_내용.getPrice())
            );
        }

        /**
         * <pre>
         * given 가격이 각각 다른 제품을 2개 등록하고 제품 가격 합계보다 10,000원 싼 메뉴를 등록한다.
         * when  하나의 제품 가격을 10,000원 인하한다.
         * then  제품 목록 조회 시 해당 제품이 조회된다.
         * then  제품의 가격은 10,000원 인하된 가격이다.
         * then  해당 메뉴는 숨김해제 상태이다.
         * </pre>
         */
        @Test
        @DisplayName("메뉴에 포함된 제품(제품가격합계 >= 메뉴가격)")
        void included_인하_display() {
            // given
            var 메뉴그룹 = 메뉴_그룹을_생성한다();

            var targetProduct = 제품을_등록한다(A_제품).as(Product.class);

            var 메뉴_제품_목록 = List.of(제품을_생성한다(1L, targetProduct, 2L));
            var 메뉴_가격 = 제품_가격합계보다_낮은_금액을_생성한다(메뉴_제품_목록);
            var 등록하고자_하는_메뉴 = 메뉴를_생성한다("A", 메뉴_가격, 메뉴그룹, true, 메뉴_제품_목록);
            var targetMenu = 메뉴를_등록한다(등록하고자_하는_메뉴).as(Menu.class);

            // when
            var 수정할_내용 = new Product();
            수정할_내용.setId(targetProduct.getId());
            수정할_내용.setPrice(targetProduct.getPrice().subtract(new BigDecimal(50)));
            제품_가격을_수정한다(수정할_내용);

            // then
            var 제품_목록 = 제품_목록을_조회한다().as(new TypeRef<List<Product>>() {});
            var 제품_optional = 제품_목록.stream().filter(product -> Objects.equals(product.getId(), targetProduct.getId()))
                    .findFirst();
            var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
            var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), targetMenu.getId()))
                    .findFirst();

            assertAll(
                    () -> assertThat(제품_optional.isPresent()).isTrue(),
                    () -> AssertUtils.가격이_동등한가(제품_optional.get().getPrice(), 수정할_내용.getPrice()),
                    () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                    () -> assertThat(메뉴_optional.get().isDisplayed()).isTrue()
            );
        }

        /**
         * <pre>
         * given 가격이 각각 다른 제품을 2개 등록하고 제품 가격 합계보다 10,000원 싼 메뉴를 등록한다.
         * when  하나의 제품 가격을 15,000원 인하한다.
         * then  제품 목록 조회 시 해당 제품이 조회된다.
         * then  제품의 가격은 15,000원 인하된 가격이다.
         * then  해당 메뉴는 숨김 상태이다.
         * </pre>
         */
        @Test
        @DisplayName("메뉴에 포함된 제품(제품가격합계 < 메뉴가격)")
        void included_인하_hide() {
            // given
            var 메뉴그룹 = 메뉴_그룹을_생성한다();

            var targetProduct = 제품을_등록한다(A_제품).as(Product.class);

            var 메뉴_제품_목록 = List.of(제품을_생성한다(1L, targetProduct, 2L));
            var 메뉴_가격 = 제품_가격합계보다_낮은_금액을_생성한다(메뉴_제품_목록);
            var 등록하고자_하는_메뉴 = 메뉴를_생성한다("A", 메뉴_가격, 메뉴그룹, true, 메뉴_제품_목록);
            var targetMenu = 메뉴를_등록한다(등록하고자_하는_메뉴).as(Menu.class);

            // when
            var 수정할_내용 = new Product();
            수정할_내용.setId(targetProduct.getId());
            수정할_내용.setPrice(new BigDecimal(100));
            제품_가격을_수정한다(수정할_내용);

            // then
            var 제품_목록 = 제품_목록을_조회한다().as(new TypeRef<List<Product>>() {});
            var 제품_optional = 제품_목록.stream().filter(product -> Objects.equals(product.getId(), targetProduct.getId()))
                    .findFirst();
            var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
            var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), targetMenu.getId()))
                    .findFirst();

            assertAll(
                    () -> assertThat(제품_optional.isPresent()).isTrue(),
                    () -> AssertUtils.가격이_동등한가(제품_optional.get().getPrice(), 수정할_내용.getPrice()),
                    () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                    () -> assertThat(메뉴_optional.get().isDisplayed()).isFalse()
            );

        }

        private static MenuGroup 메뉴_그룹을_생성한다() {
            return 메뉴_그룹을_등록한다(A_메뉴그룹).as(MenuGroup.class);
        }

    }

}
