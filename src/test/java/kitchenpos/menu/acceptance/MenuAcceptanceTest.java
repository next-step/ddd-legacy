package kitchenpos.menu.acceptance;

import io.restassured.common.mapper.TypeRef;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.menugroup.fixture.MenuGroupFixture;
import kitchenpos.product.acceptance.step.ProductStep;
import kitchenpos.product.fixture.ProductFixture;
import kitchenpos.support.AcceptanceTest;
import kitchenpos.support.util.assertion.AssertUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.menu.acceptance.step.MenuStep.메뉴_가격을_수정한다;
import static kitchenpos.menu.acceptance.step.MenuStep.메뉴_목록을_조회한다;
import static kitchenpos.menu.acceptance.step.MenuStep.메뉴를_등록한다;
import static kitchenpos.menu.acceptance.step.MenuStep.메뉴를_숨김처리_한다;
import static kitchenpos.menu.acceptance.step.MenuStep.메뉴를_숨김해제처리_한다;
import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹을_등록한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class MenuAcceptanceTest extends AcceptanceTest {

    private static final String MENU_ID_KEY = "id";

    private MenuGroup 한식_그룹, 양식_그룹;

    private Product 김치찜, 공기밥, 봉골레_파스타, 토마토_파스타, 수제_마늘빵, 피클;

    @BeforeEach
    void dataLoad() {
        한식_그룹 = 메뉴_그룹을_등록한다(MenuGroupFixture.한식).as(MenuGroup.class);
        양식_그룹 = 메뉴_그룹을_등록한다(MenuGroupFixture.양식).as(MenuGroup.class);

        김치찜 = 제품을_등록한다(ProductFixture.김치찜);
        공기밥 = 제품을_등록한다(ProductFixture.공기밥);
        봉골레_파스타 = 제품을_등록한다(ProductFixture.봉골레_파스타);
        토마토_파스타 = 제품을_등록한다(ProductFixture.토마토_파스타);
        수제_마늘빵 = 제품을_등록한다(ProductFixture.수제_마늘빵);
        피클 = 제품을_등록한다(ProductFixture.피클);
    }

    /**
     * <pre>
     * when  김치찜 1개 + 공기밥 1개 조합으로 이루어진
     *       김치찜 1인 메뉴를 24,000원에 등록한다.
     * then  메뉴 목록 조회 시 등록한 김치찜 1인 메뉴를 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 김치찜_1개 = 메뉴_제품을_생성한다(김치찜, 1L);
        var 공기밥_1개 = 메뉴_제품을_생성한다(공기밥, 1L);
        var 메뉴_제품_목록 = List.of(김치찜_1개, 공기밥_1개); // 합계: 25,000원

        var 등록할_김치찜_1인_메뉴 = 메뉴를_생성한다(
                "김치찜 1인",
                new BigDecimal(24_000),
                한식_그룹,
                true,
                메뉴_제품_목록
        );

        var 등록된_김치찜_1인_메뉴_아이디 = 메뉴를_등록한다(등록할_김치찜_1인_메뉴)
                .jsonPath()
                .getUUID(MENU_ID_KEY);

        var 메뉴_아이디_목록 = 메뉴_목록을_조회한다()
                .jsonPath()
                .getList(MENU_ID_KEY, UUID.class);
        assertThat(메뉴_아이디_목록).containsExactly(등록된_김치찜_1인_메뉴_아이디);
    }

    /**
     * <pre>
     * given 봉골레 파스타 세트 메뉴를 등록한다.
     * given 토마토 파스타 단품 메뉴를 등록한다.
     * when  메뉴 목록을 조회하면
     * then  등록한 봉골레 파스타 세트 메뉴와 토마토 파스타 단품 메뉴를 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        var 봉골레_파스타_1개 = 메뉴_제품을_생성한다(봉골레_파스타, 1L);
        var 수제_마늘빵_3개 = 메뉴_제품을_생성한다(수제_마늘빵, 3L);
        var 토마토_파스타_1개 = 메뉴_제품을_생성한다(토마토_파스타, 1L);

        var 등록할_봉골레_파스타_세트_메뉴 = 메뉴를_생성한다(
                "봉골레 파스타 세트",
                new BigDecimal(15_700),
                양식_그룹,
                true,
                List.of(봉골레_파스타_1개, 수제_마늘빵_3개)
        );
        var 등록할_토마토_파스타_단품_메뉴 = 메뉴를_생성한다(
                "토마토 파스타 단품",
                new BigDecimal(10_900),
                양식_그룹,
                true,
                List.of(토마토_파스타_1개)
        );

        var 등록된_봉골레_파스타_세트_메뉴_아이디 = 메뉴를_등록한다(등록할_봉골레_파스타_세트_메뉴)
                .jsonPath()
                .getUUID(MENU_ID_KEY);
        var 등록된_토마토_파스타_단품_메뉴_아이디 = 메뉴를_등록한다(등록할_토마토_파스타_단품_메뉴)
                .jsonPath()
                .getUUID(MENU_ID_KEY);

        // when
        var 메뉴_아이디_목록 = 메뉴_목록을_조회한다()
                .jsonPath()
                .getList(MENU_ID_KEY, UUID.class);

        // then
        assertThat(메뉴_아이디_목록).containsExactly(등록된_봉골레_파스타_세트_메뉴_아이디, 등록된_토마토_파스타_단품_메뉴_아이디);
    }

    /**
     * <pre>
     * given 메뉴를 숨김처리한 채로 등록한다.
     * when  메뉴를 숨김해제 처리를 한다.
     * then  메뉴 목록 조회 시 해당 메뉴가 조회된다.
     * then  해당 메뉴는 숨김해제 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("숨김해제처리")
    void display() {
        // given
        var 토마토_파스타_1개 = 메뉴_제품을_생성한다(토마토_파스타, 1L);

        var 등록할_토마토_파스타_단품_메뉴 = 메뉴를_생성한다(
                "토마토 파스타 단품",
                new BigDecimal(10_900),
                양식_그룹,
                false,
                List.of(토마토_파스타_1개)
        );

        var 등록된_토마토_파스타_단품_메뉴_아이디 = 메뉴를_등록한다(등록할_토마토_파스타_단품_메뉴)
                .jsonPath()
                .getUUID(MENU_ID_KEY);

        // when
        메뉴를_숨김해제처리_한다(등록된_토마토_파스타_단품_메뉴_아이디);

        // then
        var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {
        });
        var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), 등록된_토마토_파스타_단품_메뉴_아이디))
                .findFirst();

        assertAll(
                () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                () -> assertThat(메뉴_optional.get().isDisplayed()).isTrue()
        );
    }

    /**
     * <pre>
     * given 메뉴를 숨김해제 처리한 채로 등록한다.
     * when  메뉴를 숨김 처리를 하면
     * then  메뉴 목록 조회 시 해당 메뉴가 조회된다.
     * then  해당 메뉴는 숨김해제 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("숨김처리")
    void hide() {
        // given
        var 피클_5개 = 메뉴_제품을_생성한다(피클, 5L);

        var 등록할_피클_메뉴 = 메뉴를_생성한다(
                "피클",
                new BigDecimal(500),
                양식_그룹,
                true,
                List.of(피클_5개)
        );

        var 등록된_피클_메뉴_아이디 = 메뉴를_등록한다(등록할_피클_메뉴)
                .jsonPath()
                .getUUID(MENU_ID_KEY);

        // when
        메뉴를_숨김처리_한다(등록된_피클_메뉴_아이디);

        // then
        var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {
        });
        var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), 등록된_피클_메뉴_아이디))
                .findFirst();

        assertAll(
                () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                () -> assertThat(메뉴_optional.get().isDisplayed()).isFalse()
        );
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
        // given
        var 김치찜_1개 = 메뉴_제품을_생성한다(김치찜, 1L);
        var 공기밥_1개 = 메뉴_제품을_생성한다(공기밥, 1L);
        var 메뉴_제품_목록 = List.of(김치찜_1개, 공기밥_1개); // 합계: 25,000원

        var 등록할_김치찜_1인_메뉴 = 메뉴를_생성한다(
                "김치찜 1인",
                new BigDecimal(24_000),
                한식_그룹,
                true,
                메뉴_제품_목록
        );

        var 등록된_김치찜_1인_메뉴_아이디 = 메뉴를_등록한다(등록할_김치찜_1인_메뉴)
                .jsonPath()
                .getUUID(MENU_ID_KEY);

        // when
        var 수정_가격 = new BigDecimal(24_500);

        var 수정할_내용 = new Menu();
        수정할_내용.setId(등록된_김치찜_1인_메뉴_아이디);
        수정할_내용.setPrice(수정_가격);
        메뉴_가격을_수정한다(수정할_내용);

        // then
        var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
        var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), 등록된_김치찜_1인_메뉴_아이디))
                .findFirst();

        assertAll(
                () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                () -> AssertUtils.가격이_동등한가(메뉴_optional.get().getPrice(), 수정_가격)
        );
    }

    private Product 제품을_등록한다(Product 등록하고자_하는_제품) {
        return ProductStep.제품을_등록한다(등록하고자_하는_제품).as(Product.class);
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
