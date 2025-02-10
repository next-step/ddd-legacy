package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(UUID menuGroupId, String name, BigDecimal price, List<MenuProduct> menuProducts) {
        return createMenu(null, null, menuGroupId, name, price, false, menuProducts);
    }

    public static Menu createMenu(UUID id, MenuGroup menuGroup, UUID menuGroupId, String name, BigDecimal price, boolean displayed, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", id);
        ReflectionTestUtils.setField(menu, "menuGroup", menuGroup);
        ReflectionTestUtils.setField(menu, "menuGroupId", menuGroupId);
        ReflectionTestUtils.setField(menu, "name", name);
        ReflectionTestUtils.setField(menu, "price", price);
        ReflectionTestUtils.setField(menu, "displayed", displayed);
        ReflectionTestUtils.setField(menu, "menuProducts", menuProducts);
        return menu;
    }

    private MenuFixture() {
    }
}
