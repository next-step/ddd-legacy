package kitchenpos.application;

import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuProductFixture.맛초킹_1개;
import static kitchenpos.application.MenuProductFixture.뿌링클_1개;
import static kitchenpos.application.MenuProductFixture.콜라_1개;

import java.math.BigDecimal;
import java.util.Arrays;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    public static final Menu 뿌링클_세트 = new Menu();
    public static final Menu 맛초킹_세트 = new Menu();

    static {
        initialize(뿌링클_세트, "뿌링클 세트", 뿌링클_1개);
        initialize(맛초킹_세트, "맛초킹 세트", 맛초킹_1개);
    }

    private static void initialize(Menu menu, String name, MenuProduct menuProduct) {
        menu.setName(name);
        menu.setMenuGroup(세트메뉴);
        menu.setMenuGroupId(세트메뉴.getId());
        menu.setMenuProducts(Arrays.asList(menuProduct, 콜라_1개));
        menu.setPrice(BigDecimal.valueOf(11_000L));
        menu.setDisplayed(true);
    }

}
