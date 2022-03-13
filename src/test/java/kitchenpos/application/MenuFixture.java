package kitchenpos.application;

import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuProductFixture.뿌링클_1개;
import static kitchenpos.application.MenuProductFixture.콜라_1개;

import java.math.BigDecimal;
import java.util.Arrays;
import kitchenpos.domain.Menu;

public class MenuFixture {

    public static final Menu 뿌링클_세트 = new Menu();

    static {
        뿌링클_세트.setName("뿌링클 세트");
        뿌링클_세트.setMenuGroup(세트메뉴);
        뿌링클_세트.setMenuGroupId(세트메뉴.getId());
        뿌링클_세트.setMenuProducts(Arrays.asList(뿌링클_1개, 콜라_1개));
        뿌링클_세트.setPrice(BigDecimal.valueOf(11_000L));
        뿌링클_세트.setDisplayed(true);
    }

}
