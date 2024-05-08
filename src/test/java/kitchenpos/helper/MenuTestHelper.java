package kitchenpos.helper;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuTestHelper {
    private static MenuRepository menuRepository;

    public MenuTestHelper(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public static Menu 메뉴_생성(MenuGroup menuGroup, String name, BigDecimal price, List<MenuProduct> menuProducts, boolean displayed){
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(displayed);

        return menuRepository.save(menu);
    }

    public static List<Menu> 특정음식이_속한_메뉴들_조회(UUID productId){
        return menuRepository.findAllByProductId(productId);
    }
}
