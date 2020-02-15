package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kitchenpos.menu.group.supports.MenuGroupDaoWithConstraint;
import kitchenpos.model.MenuGroup;

class MenuGroupBoTest {

    private static final String VALID_NAME = "그룹";

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void create() {
        MenuGroupBo cut = new MenuGroupBo(MenuGroupDaoWithConstraint.withCollection());
        MenuGroup menuGroup = menuGroupFrom(VALID_NAME);

        assertThat(cut.create(menuGroup))
            .isSameAs(menuGroup);
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다. 메뉴 그룹 이름은 필수이다.")
    void create_when_name_is_null() {
        MenuGroupBo cut = new MenuGroupBo(MenuGroupDaoWithConstraint.withCollection());

        assertThatThrownBy(() -> cut.create(menuGroupFrom(null)))
            .isExactlyInstanceOf(MenuGroupDaoWithConstraint.MENU_GROUP_CONSTRAINT_EXCEPTION.getClass());
    }

    private static MenuGroup menuGroupFrom(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName(name);
        return menuGroup;
    }
}
