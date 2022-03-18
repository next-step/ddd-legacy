package kitchenpos.domain;

public class MenuGroupTest {

    public static MenuGroup create(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
