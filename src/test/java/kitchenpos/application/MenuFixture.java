package kitchenpos.application;

import static kitchenpos.application.MenuProductFixture.메뉴_뿌링클;
import static kitchenpos.application.MenuProductFixture.메뉴_콜라;

import java.math.BigDecimal;
import java.util.Arrays;
import kitchenpos.domain.Menu;

public class MenuFixture {

    public static final Menu 뿌링클_세트 = new Menu();

    static {
        뿌링클_세트.setMenuProducts(Arrays.asList(메뉴_뿌링클, 메뉴_콜라));
        뿌링클_세트.setPrice(BigDecimal.valueOf(11_000L));
        뿌링클_세트.setDisplayed(true);
    }

}
