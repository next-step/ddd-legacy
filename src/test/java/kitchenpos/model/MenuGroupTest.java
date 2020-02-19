package kitchenpos.model;

public class MenuGroupTest {
    public static MenuGroup ofSet() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("세트메뉴");
        return menuGroup;
    }
}