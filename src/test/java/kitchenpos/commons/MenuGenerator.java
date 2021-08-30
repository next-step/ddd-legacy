package kitchenpos.commons;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MenuGenerator {
    @Autowired
    private MenuService menuService;

    public Menu generateByMenuGroupAndMenuProducts(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName("menu");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setDisplayed(true);

        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menuService.create(menu);
    }
}
