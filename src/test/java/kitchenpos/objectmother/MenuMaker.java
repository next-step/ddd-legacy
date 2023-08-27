package kitchenpos.objectmother;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuMaker {

    public static Menu make(String name, Long price, MenuGroup menuGroup, MenuProduct... menuProducts) {
        return new Menu(name, new BigDecimal(price), menuGroup, true, List.of(menuProducts), menuGroup.getId());
    }

}
