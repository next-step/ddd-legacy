package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


import static kitchenpos.menu.fixture.MenuProductFixture.공기밥_1개;
import static kitchenpos.menu.fixture.MenuProductFixture.김치찜_1개;
import static kitchenpos.menu.fixture.MenuProductFixture.마이너스수량_메뉴제품;
import static kitchenpos.menu.fixture.MenuProductFixture.봉골레_파스타_1개;
import static kitchenpos.menu.fixture.MenuProductFixture.수제_마늘빵_3개;
import static kitchenpos.menu.fixture.MenuProductFixture.토마토_파스타_1개;
import static kitchenpos.menu.fixture.MenuProductFixture.피클_1개;
import static kitchenpos.menu.fixture.MenuProductFixture.피클_3개;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.양식;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.한식;


public class MenuFixture {

    public static final Menu 김치찜_1인_메뉴 = 메뉴를_생성한다(
            "김치찜 1인",
            new BigDecimal(24_000),
            한식,
            true,
            List.of(김치찜_1개, 공기밥_1개) // 합계: 25,000원
    );

    public static final Menu 봉골레_파스타_세트_메뉴 = 메뉴를_생성한다(
            "봉골레 파스타 세트",
            new BigDecimal(15_700),
            양식,
            true,
            List.of(봉골레_파스타_1개, 수제_마늘빵_3개) // 합계: 18,000원
    );

    public static final Menu 토마토_파스타_단품_메뉴 = 메뉴를_생성한다(
            "토마토 파스타 단품",
            new BigDecimal(10_900),
            양식,
            true,
            List.of(토마토_파스타_1개) // 합계: 11,000원
    );

    public static final Menu 가격미존재_메뉴 = 메뉴를_생성한다(
            "가격이 존재하지 않은 메뉴",
            null,
            양식,
            true,
            List.of(피클_1개)
    );

    public static final Menu 가격마이너스_메뉴 = 메뉴를_생성한다(
            "가격이 마이너스인 제품",
            new BigDecimal(-1_000),
            양식,
            true,
            List.of(피클_1개)
    );

    public static final Menu 메뉴가격이_제품목록의_가격합계보다_높은메뉴 = 메뉴를_생성한다(
            "메뉴 가격 > 제품 가격 합계",
            new BigDecimal(30_000),
            한식,
            false,
            List.of(김치찜_1개)
    );

    public static final Menu 제품목록미존재_메뉴 = 메뉴를_생성한다(
            "제품 목록이 존재하지 않은 메뉴",
            BigDecimal.ZERO,
            양식,
            true,
            null
    );

    public static final Menu 빈_제품목록_메뉴 = 메뉴를_생성한다(
            "제품 목록이 비어있는 메뉴",
            BigDecimal.ZERO,
            양식,
            true,
            Collections.emptyList()
    );

    public static final Menu 마이너스_수량의_제품을_가진_메뉴 = 메뉴를_생성한다(
            "마이너스 수량의 제품이 포함된 메뉴",
            BigDecimal.ZERO,
            양식,
            true,
            List.of(마이너스수량_메뉴제품)
    );

    public static final Menu 이름미존재_메뉴 = 메뉴를_생성한다(
            null,
            new BigDecimal(1_000),
            한식,
            true,
            List.of(공기밥_1개)
    );

    public static final Menu 욕설이름_메뉴 = 메뉴를_생성한다(
            "욕설",
            new BigDecimal(11_000),
            양식,
            true,
            List.of(봉골레_파스타_1개)
    );

    public static final Menu 피클_메뉴_숨김 = 메뉴를_생성한다(
            "피클",
            new BigDecimal(1_000),
            양식,
            false,
            List.of(피클_3개)
    );

    public static final Menu 토마토_파스타_단품_메뉴_숨김해제 = 메뉴를_생성한다(
            "토마토 파스타 단품",
            new BigDecimal(10_900),
            양식,
            true,
            List.of(토마토_파스타_1개) // 합계: 11,000원
    );

    private static Menu 메뉴를_생성한다(
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
