package kitchenpos.model;

public class MenuGroupTest {
    static final Long SET_MENU_GROUP_ID = 1L;

    public static MenuGroup ofSet() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(SET_MENU_GROUP_ID);
        menuGroup.setName("세트메뉴");
        return menuGroup;
    }
}
