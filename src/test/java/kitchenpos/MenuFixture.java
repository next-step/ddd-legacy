package kitchenpos;

import helper.IdGenerator;
import helper.PriceGenerator;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuFixture {

    private static final IdGenerator menuIdGenerator = UUID::randomUUID;
    private static final PriceGenerator priceGenerator = BigDecimal::new;

    public static Menu 후라이드_치킨_메뉴() {
        Menu menu = new Menu();
        menu.setId(menuIdGenerator.ramdom());
        menu.setName("후라이드 치킨 메뉴");
        menu.setPrice(priceGenerator.of(16000));
        return menu;
    }

}
