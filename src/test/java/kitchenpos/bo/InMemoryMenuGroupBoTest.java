package kitchenpos.bo;

import kitchenpos.dao.InMemoryMenuGroupDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import kitchenpos.support.MenuGroupBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryMenuGroupBoTest {

    private MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    private MenuGroupBo menuGroupBo;

    @BeforeEach
    void setup (){
        menuGroupBo = new MenuGroupBo(menuGroupDao);
    }

    @DisplayName("메뉴그룹을 등록한다.")
    @Test
    void create(){
        MenuGroup menuGroup = new MenuGroupBuilder()
            .id(1L)
            .name("세마리메뉴")
            .build();

        assertThat(menuGroupBo.create(menuGroup)).isEqualToComparingFieldByField(menuGroupBo);
    }

    @DisplayName("등록한 메뉴 그룹을 조회한다.")
    @Test
    void list(){
        List<MenuGroup> menuGroupList = new ArrayList<>();
        MenuGroup menuGroup1 = new MenuGroupBuilder()
            .id(1L)
            .name("세마리메뉴")
            .build();

        menuGroupBo.create(menuGroup1);
        menuGroupList.add(menuGroup1);

        MenuGroup menuGroup2 = new MenuGroupBuilder()
            .id(2L)
            .name("네마리메뉴")
            .build();

        menuGroupBo.create(menuGroup2);
        menuGroupList.add(menuGroup2);

        List<MenuGroup> savedMenuGroups = menuGroupBo.list();

        assertThat(savedMenuGroups).isEqualTo(menuGroupList);
    }

}
