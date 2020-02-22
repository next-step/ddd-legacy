package kitchenpos.bo;

import kitchenpos.bo.mock.TestMenuGroupDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kitchenpos.Fixture.defaultMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;

class MenuGroupBoTest {

    private static final String MENU_GROUP_NAME = "신메뉴";

    private MenuGroupDao menuGroupDao = new TestMenuGroupDao();
    private MenuGroupBo menuGroupBo = new MenuGroupBo(menuGroupDao);

    @DisplayName("메뉴 그룹 저장")
    @Test
    void create() {
        MenuGroup input = new MenuGroup();
        input.setId(1L);
        input.setName(MENU_GROUP_NAME);

        MenuGroup result = menuGroupBo.create(input);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo(MENU_GROUP_NAME);
    }

    @DisplayName("메뉴 그룹 조회")
    @Test
    void list() {
        menuGroupDao.save(defaultMenuGroup());

        List<MenuGroup> result = menuGroupBo.list();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(MENU_GROUP_NAME);
    }
}
