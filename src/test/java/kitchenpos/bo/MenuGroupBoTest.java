package kitchenpos.bo;

import kitchenpos.builder.MenuGroupBuilder;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {
    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @Test
    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    public void createMenuGroup() {
        MenuGroup newMenuGroup = new MenuGroupBuilder()
                .id(1L)
                .name("두마리메뉴")
                .build();
        MenuGroup savedMenuGroup = new MenuGroupBuilder()
                .id(1L)
                .name("두마리메뉴")
                .build();

        given(menuGroupDao.save(any(MenuGroup.class)))
                .willReturn(newMenuGroup);

        assertThat(menuGroupBo.create(newMenuGroup))
                .isEqualToComparingFieldByField(savedMenuGroup);
    }

    @Test
    @DisplayName("메뉴 그룹을 조회할 수 있다.")
    public void listMenuGroup() {
        List<MenuGroup> menuGroupList = newArrayList();

        menuGroupList.add(new MenuGroupBuilder()
                .id(1L)
                .name("한마리메뉴")
                .build());
        menuGroupList.add(new MenuGroupBuilder()
                .id(2L)
                .name("두마리메뉴")
                .build());

        given(menuGroupDao.findAll())
                .willReturn(menuGroupList);

        assertThat(menuGroupBo.list())
                .isEqualTo(menuGroupList);
    }
}
