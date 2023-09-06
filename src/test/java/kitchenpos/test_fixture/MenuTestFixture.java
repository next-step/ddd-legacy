package kitchenpos.test_fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MenuTestFixture {
    private Menu menu;

    private MenuTestFixture(Menu menu) {
        this.menu = menu;
    }

    public static MenuTestFixture create() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("테스트 메뉴");
        menu.setPrice(BigDecimal.valueOf(10000));
        menu.setMenuGroup(MenuGroupTestFixture.create().getMenuGroup());
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setMenuProducts(Collections.singletonList(MenuProductTestFixture.create().getMenuProduct()));
        return new MenuTestFixture(menu);
    }

    public MenuTestFixture changeId(UUID id) {
        Menu newMenu = new Menu();
        newMenu.setId(id);
        newMenu.setName(menu.getName());
        newMenu.setPrice(menu.getPrice());
        newMenu.setMenuGroup(menu.getMenuGroup());
        newMenu.setMenuGroupId(menu.getMenuGroupId());
        newMenu.setDisplayed(menu.isDisplayed());
        newMenu.setMenuProducts(menu.getMenuProducts());
        this.menu = newMenu;
        return this;
    }

    public MenuTestFixture changeName(String name) {
        Menu newMenu = new Menu();
        newMenu.setId(menu.getId());
        newMenu.setName(name);
        newMenu.setPrice(menu.getPrice());
        newMenu.setMenuGroup(menu.getMenuGroup());
        newMenu.setMenuGroupId(menu.getMenuGroupId());
        newMenu.setDisplayed(menu.isDisplayed());
        newMenu.setMenuProducts(menu.getMenuProducts());
        this.menu = newMenu;
        return this;
    }

    public MenuTestFixture changePrice(BigDecimal price) {
        Menu newMenu = new Menu();
        newMenu.setId(menu.getId());
        newMenu.setName(menu.getName());
        newMenu.setPrice(price);
        newMenu.setMenuGroup(menu.getMenuGroup());
        newMenu.setMenuGroupId(menu.getMenuGroupId());
        newMenu.setDisplayed(menu.isDisplayed());
        newMenu.setMenuProducts(menu.getMenuProducts());
        this.menu = newMenu;
        return this;
    }

    public MenuTestFixture changeMenuGroup(MenuGroup menuGroup) {
        Menu newMenu = new Menu();
        newMenu.setId(menu.getId());
        newMenu.setName(menu.getName());
        newMenu.setPrice(menu.getPrice());
        newMenu.setMenuGroup(menuGroup);
        newMenu.setMenuGroupId(menuGroup.getId());
        newMenu.setDisplayed(menu.isDisplayed());
        newMenu.setMenuProducts(menu.getMenuProducts());
        this.menu = newMenu;
        return this;
    }

    public MenuTestFixture changeDisplayed(boolean displayed) {
        Menu newMenu = new Menu();
        newMenu.setId(menu.getId());
        newMenu.setName(menu.getName());
        newMenu.setPrice(menu.getPrice());
        newMenu.setMenuGroup(menu.getMenuGroup());
        newMenu.setMenuGroupId(menu.getMenuGroupId());
        newMenu.setDisplayed(displayed);
        newMenu.setMenuProducts(menu.getMenuProducts());
        this.menu = newMenu;
        return this;
    }

    public MenuTestFixture changeMenuProducts(List<MenuProduct> menuProduct) {
        Menu newMenu = new Menu();
        newMenu.setId(menu.getId());
        newMenu.setName(menu.getName());
        newMenu.setPrice(menu.getPrice());
        newMenu.setMenuGroup(menu.getMenuGroup());
        newMenu.setMenuGroupId(menu.getMenuGroupId());
        newMenu.setDisplayed(menu.isDisplayed());
        newMenu.setMenuProducts(menuProduct);
        this.menu = newMenu;
        return this;
    }

    public Menu getMenu() {
        return menu;
    }
}
