package kitchenpos.menu.menugroup;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menugroup.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MenuGroupFixture {

    public static MenuGroup menuGroup() {
        return new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹", false));
    }
}
