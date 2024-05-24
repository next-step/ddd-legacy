package kitchenpos.product.acceptance;

import io.restassured.common.mapper.TypeRef;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.menugroup.acceptance.step.MenuGroupStep;
import kitchenpos.support.AcceptanceTest;
import kitchenpos.support.util.assertion.AssertUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.menu.acceptance.step.MenuStep.메뉴_목록을_조회한다;
import static kitchenpos.menu.acceptance.step.MenuStep.메뉴를_등록한다;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.양식;
import static kitchenpos.product.acceptance.step.ProductStep.제품_가격을_수정한다;
import static kitchenpos.product.acceptance.step.ProductStep.제품_목록을_조회한다;
import static kitchenpos.product.acceptance.step.ProductStep.제품을_등록한다;
import static kitchenpos.product.fixture.ProductFixture.공기밥;
import static kitchenpos.product.fixture.ProductFixture.김치찜;
import static kitchenpos.product.fixture.ProductFixture.봉골레_파스타;
import static kitchenpos.product.fixture.ProductFixture.수제_마늘빵;
import static kitchenpos.product.fixture.ProductFixture.토마토_파스타;
import static kitchenpos.product.fixture.ProductFixture.피클;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductAcceptanceTest extends AcceptanceTest {

    private static final String PRODUCT_ID_KEY = "id";

    /**
     * <pre>
     * when 김치찜을 등록한다.
     * then 제품 목록 조회 시 김치찜을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 등록된_제품 = 제품을_등록한다(김치찜).as(Product.class);

        // then
        var 제품_아이디_목록 = 제품_목록을_조회한다()
                .jsonPath()
                .getList(PRODUCT_ID_KEY, UUID.class);
        assertThat(제품_아이디_목록).containsExactly(등록된_제품.getId());
    }

    /**
     * <pre>
     * given 김치찜과 공기밥을 등록한다.
     * when  제품 목록을 조회한다.
     * then  김치찜과 공기밥을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        var 등록된_제품_김치찜 = 제품을_등록한다(김치찜).as(Product.class);
        var 등록된_제품_공기밥 = 제품을_등록한다(공기밥).as(Product.class);

        // when
        var 제품_아이디_목록 = 제품_목록을_조회한다()
                .jsonPath()
                .getList(PRODUCT_ID_KEY, UUID.class);

        // then
        assertThat(제품_아이디_목록).containsExactly(등록된_제품_김치찜.getId(), 등록된_제품_공기밥.getId());
    }

    @Nested
    class 가격수정 {

        /**
         * <pre>
         * given 가격이 1,000원인 피클을 등록한다.
         * when  피클의 가격을 500원으로 인하한다.
         * then  제품 목록 조회 시 피클이 조회된다.
         * then  피클의 가격은 500원이다.
         * </pre>
         */
        @Test
        @DisplayName("메뉴에 포함되지 않은 제품")
        void standalone() {
            // given
            var 등록된_제품_피클 = 제품을_등록한다(피클).as(Product.class);

            // when
            var 타겟_아이디 = 등록된_제품_피클.getId();
            var 변경가격 = new BigDecimal(500);

            var 수정할_내용 = new Product();
            수정할_내용.setId(타겟_아이디);
            수정할_내용.setPrice(변경가격);

            제품_가격을_수정한다(수정할_내용);

            // then
            var 제품_목록 = 제품_목록을_조회한다().as(new TypeRef<List<Product>>() {});
            var 제품_optional = 제품_목록.stream().filter(product -> Objects.equals(product.getId(), 타겟_아이디))
                    .findFirst();

            assertAll(
                    () -> assertThat(제품_optional.isPresent()).isTrue(),
                    () -> AssertUtils.가격이_동등한가(제품_optional.get().getPrice(), 변경가격)
            );
        }

        /**
         * <pre>
         * given 가격이 12,000원인 봉골레 파스타를 등록한다.
         * given 가격이 2,000원인 수제 마늘빵을 등록한다.
         * given 봉골레 파스타 1개 + 수제 마늘빵 3개 조합으로 이루어진
         *       봉골레 파스타 세트 메뉴를 16,200원에 등록한다.
         * when  수제 마늘빵을 1,500원으로 인하한다.
         * then  제품 목록 조회 시 수제 마늘빵이 조회된다.
         * then  수제 마늘빵의 가격은 1,500원이다.
         * then  봉골레 파스타 세트 메뉴는 숨김해제 상태이다.
         * </pre>
         */
        @Test
        @DisplayName("메뉴에 포함된 제품(제품가격합계 >= 메뉴가격)")
        void included_인하_display() {
            // given
            var 등록된_제품_봉골레_파스타 = 제품을_등록한다(봉골레_파스타).as(Product.class);
            var 등록된_제품_수제_마늘빵 = 제품을_등록한다(수제_마늘빵).as(Product.class);
            var 등록된_봉골레_파스타_세트_메뉴_아이디 = 봉골레_파스타_세트_메뉴를_등록한다(등록된_제품_봉골레_파스타, 등록된_제품_수제_마늘빵)
                    .getId();

            // when
            var 타겟_아이디 = 등록된_제품_수제_마늘빵.getId();
            var 수정_가격 = new BigDecimal(1_500);

            var 수정할_내용 = new Product();
            수정할_내용.setId(타겟_아이디);
            수정할_내용.setPrice(수정_가격);

            제품_가격을_수정한다(수정할_내용);

            // then
            var 제품_목록 = 제품_목록을_조회한다().as(new TypeRef<List<Product>>() {});
            var 제품_optional = 제품_목록.stream().filter(product -> Objects.equals(product.getId(), 타겟_아이디))
                    .findFirst();
            var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
            var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), 등록된_봉골레_파스타_세트_메뉴_아이디))
                    .findFirst();

            assertAll(
                    () -> assertThat(제품_optional.isPresent()).isTrue(),
                    () -> AssertUtils.가격이_동등한가(제품_optional.get().getPrice(), 수정_가격),
                    () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                    () -> assertThat(메뉴_optional.get().isDisplayed()).isTrue()
            );
        }

        /**
         * <pre>
         * given 가격이 11,000원인 토마토 파스타를 등록한다.
         * given 토마토 파스타 단품 메뉴를 10,900원에 등록한다.
         * when  토마토 파스타를 10,500원으로 인하한다.
         * then  제품 목록 조회 시 토마토 파스타가 조회된다.
         * then  토마토 파스타의 가격은 10,500원이다.
         * then  토마토 파스타 단품 메뉴는 숨김 상태이다.
         * </pre>
         */
        @Test
        @DisplayName("메뉴에 포함된 제품(제품가격합계 < 메뉴가격)")
        void included_인하_hide() {
            // given
            var 등록된_제품_토마토_파스타 = 제품을_등록한다(토마토_파스타).as(Product.class);
            var 등록된_토마토_파스타_단품_메뉴_아이디 = 토마토_파스타_단품_메뉴를_등록한다(등록된_제품_토마토_파스타)
                    .getId();

            // when
            var 타겟_아이디 = 등록된_제품_토마토_파스타.getId();
            var 수정_가격 = new BigDecimal(10_500);

            var 수정할_내용 = new Product();
            수정할_내용.setId(타겟_아이디);
            수정할_내용.setPrice(수정_가격);

            제품_가격을_수정한다(수정할_내용);

            // then
            var 제품_목록 = 제품_목록을_조회한다().as(new TypeRef<List<Product>>() {});
            var 제품_optional = 제품_목록.stream().filter(product -> Objects.equals(product.getId(), 타겟_아이디))
                    .findFirst();
            var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
            var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), 등록된_토마토_파스타_단품_메뉴_아이디))
                    .findFirst();

            assertAll(
                    () -> assertThat(제품_optional.isPresent()).isTrue(),
                    () -> AssertUtils.가격이_동등한가(제품_optional.get().getPrice(), 수정_가격),
                    () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                    () -> assertThat(메뉴_optional.get().isDisplayed()).isFalse()
            );
        }

        private Menu 봉골레_파스타_세트_메뉴를_등록한다(Product 등록된_봉골레_파스타, Product 등록된_수제_마늘빵) {
            var 양식_그룹 = 양식_그룹을_등록한다();

            var 봉골레_파스타_1개 = 메뉴_제품을_생성한다(등록된_봉골레_파스타, 1L);
            var 수제_마늘빵_3개 = 메뉴_제품을_생성한다(등록된_수제_마늘빵, 3L);
            var 메뉴_제품_목록 = List.of(봉골레_파스타_1개, 수제_마늘빵_3개); // 합계: 18,000원

            var 등록할_봉골레_파스타_세트_메뉴 = 메뉴를_생성한다(
                    "봉골레 파스타 세트",
                    new BigDecimal(15_700),
                    양식_그룹,
                    true,
                    메뉴_제품_목록
            );

            return 메뉴를_등록한다(등록할_봉골레_파스타_세트_메뉴).as(Menu.class);
        }

        private Menu 토마토_파스타_단품_메뉴를_등록한다(Product 등록된_토마토_파스타) {
            var 양식_그룹 = 양식_그룹을_등록한다();
            var 토마토_파스타_1개 = 메뉴_제품을_생성한다(등록된_토마토_파스타, 1L);

            var 등록할_토마토_파스타_단품_메뉴 = 메뉴를_생성한다(
                    "토마토 파스타 단품",
                    new BigDecimal(10_900),
                    양식_그룹,
                    true,
                    List.of(토마토_파스타_1개) // 합계: 11,000원
            );

            return 메뉴를_등록한다(등록할_토마토_파스타_단품_메뉴).as(Menu.class);
        }

        private MenuGroup 양식_그룹을_등록한다() {
            return MenuGroupStep.메뉴_그룹을_등록한다(양식).as(MenuGroup.class);
        }

        private MenuProduct 메뉴_제품을_생성한다(Product product, Long quantity) {
            var 메뉴_제품 = new MenuProduct();
            메뉴_제품.setProductId(product.getId());
            메뉴_제품.setProduct(product);
            메뉴_제품.setQuantity(quantity);

            return 메뉴_제품;
        }

        private Menu 메뉴를_생성한다(
                String name,
                BigDecimal price,
                MenuGroup menuGroup,
                boolean displayed,
                List<MenuProduct> products) {
            var 메뉴 = new Menu();
            메뉴.setName(name);
            메뉴.setPrice(price);
            메뉴.setMenuGroupId(menuGroup.getId());
            메뉴.setMenuGroup(menuGroup);
            메뉴.setDisplayed(displayed);
            메뉴.setMenuProducts(products);

            return 메뉴;
        }

    }

}
