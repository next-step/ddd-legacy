package kitchenpos.menu.acceptance;

import io.restassured.common.mapper.TypeRef;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.support.AcceptanceTest;
import kitchenpos.support.AssertUtils;
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
import static kitchenpos.menu.fixture.MenuFixture.메뉴를_생성한다;
import static kitchenpos.menu.fixture.MenuFixture.제품_가격합계보다_낮은_금액을_생성한다;
import static kitchenpos.menu.fixture.MenuFixture.제품을_생성한다;
import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹을_등록한다;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.A_메뉴그룹;
import static kitchenpos.product.acceptance.step.ProductStep.제품을_등록한다;
import static kitchenpos.product.fixture.ProductFixture.A_제품;
import static kitchenpos.product.fixture.ProductFixture.B_제품;
import static kitchenpos.support.RandomQuantityUtil.랜덤한_5개_이하의_수량을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class MenuAcceptanceTest extends AcceptanceTest {

    private static final String MENU_ID_KEY = "id";

    /**
     * <pre>
     * when 메뉴를 등록하면
     * then 메뉴 목록 조회 시 등록한 메뉴를 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // given
        var 메뉴그룹 = 메뉴_그룹을_생성한다();
        var 제품목록 = 제품_목록을_생성한다();
        var 가격 = 제품_가격합계보다_낮은_금액을_생성한다(제품목록);
        var 등록하고자_하는_메뉴 = 메뉴를_생성한다("A", 가격, 메뉴그룹, true, 제품목록);

        // when
        var 등록된_메뉴 = 메뉴를_등록한다(등록하고자_하는_메뉴).as(Menu.class);

        var 메뉴_아이디_목록 = 메뉴_목록을_조회한다()
                .jsonPath()
                .getList(MENU_ID_KEY, UUID.class);
        assertThat(메뉴_아이디_목록).containsExactly(등록된_메뉴.getId());
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
        // given
        var 메뉴그룹 = 메뉴_그룹을_생성한다();
        var 제품목록 = 제품_목록을_생성한다();
        var 가격 = 제품_가격합계보다_낮은_금액을_생성한다(제품목록);
        var 등록하고자_하는_메뉴_A = 메뉴를_생성한다("A", 가격, 메뉴그룹, true, 제품목록);
        var 등록하고자_하는_메뉴_B = 메뉴를_생성한다("B", 가격, 메뉴그룹, false, 제품목록);
        var 등록된_메뉴_A = 메뉴를_등록한다(등록하고자_하는_메뉴_A).as(Menu.class);
        var 등록된_메뉴_B = 메뉴를_등록한다(등록하고자_하는_메뉴_B).as(Menu.class);

        // when
        var 메뉴_아이디_목록 = 메뉴_목록을_조회한다()
                .jsonPath()
                .getList(MENU_ID_KEY, UUID.class);

        // then
        assertThat(메뉴_아이디_목록).containsExactly(등록된_메뉴_A.getId(), 등록된_메뉴_B.getId());
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
        // given
        var 메뉴그룹 = 메뉴_그룹을_생성한다();
        var 제품목록 = 제품_목록을_생성한다();
        var 가격 = 제품_가격합계보다_낮은_금액을_생성한다(제품목록);
        var 등록하고자_하는_메뉴 = 메뉴를_생성한다("C", 가격, 메뉴그룹, false, 제품목록);
        var target = 메뉴를_등록한다(등록하고자_하는_메뉴).
                jsonPath()
                .getUUID(MENU_ID_KEY);

        // when
        메뉴를_숨김해제처리_한다(target);

        // then
        var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
        var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), target))
                .findFirst();

        assertAll(
                () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                () -> assertThat(메뉴_optional.get().isDisplayed()).isTrue()
        );
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
        // given
        var 메뉴그룹 = 메뉴_그룹을_생성한다();
        var 제품목록 = 제품_목록을_생성한다();
        var 가격 = 제품_가격합계보다_낮은_금액을_생성한다(제품목록);
        var 등록하고자_하는_메뉴 = 메뉴를_생성한다("D", 가격, 메뉴그룹, true, 제품목록);
        var target = 메뉴를_등록한다(등록하고자_하는_메뉴).
                jsonPath()
                .getUUID(MENU_ID_KEY);

        // when
        메뉴를_숨김처리_한다(target);

        // then
        var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
        var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), target))
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
        var 메뉴그룹 = 메뉴_그룹을_생성한다();
        var 제품목록 = 제품_목록을_생성한다();
        var 가격 = 제품_가격합계보다_낮은_금액을_생성한다(제품목록);
        var 등록하고자_하는_메뉴 = 메뉴를_생성한다("E", 가격, 메뉴그룹, true, 제품목록);
        var target = 메뉴를_등록한다(등록하고자_하는_메뉴).as(Menu.class);

        // when
        var 수정할_내용 = new Menu();
        수정할_내용.setId(target.getId());
        수정할_내용.setPrice(target.getPrice().subtract(new BigDecimal(100)));
        메뉴_가격을_수정한다(수정할_내용);

        // then
        var 메뉴_목록 = 메뉴_목록을_조회한다().as(new TypeRef<List<Menu>>() {});
        var 메뉴_optional = 메뉴_목록.stream().filter(menu -> Objects.equals(menu.getId(), target.getId()))
                .findFirst();

        assertAll(
                () -> assertThat(메뉴_optional.isPresent()).isTrue(),
                () -> AssertUtils.가격이_동등한가(메뉴_optional.get().getPrice(), 수정할_내용.getPrice())
        );
    }

    private static MenuGroup 메뉴_그룹을_생성한다() {
        return 메뉴_그룹을_등록한다(A_메뉴그룹).as(MenuGroup.class);
    }

    private static List<MenuProduct> 제품_목록을_생성한다() {
        var 등록된_A_제품 = 제품을_등록한다(A_제품).as(Product.class);
        var A_메뉴그룹 = 제품을_생성한다(1L, 등록된_A_제품, 랜덤한_5개_이하의_수량을_생성한다());

        var 등록된_B_제품 = 제품을_등록한다(B_제품).as(Product.class);
        var B_메뉴그룹 = 제품을_생성한다(2L, 등록된_B_제품, 랜덤한_5개_이하의_수량을_생성한다());

        return List.of(A_메뉴그룹, B_메뉴그룹);
    }
}
