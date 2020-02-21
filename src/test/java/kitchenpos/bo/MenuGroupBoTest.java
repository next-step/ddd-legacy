package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {
    public static final long LONG_ONE = 1L;
    public static final long LONG_TWO = 2L;

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("메뉴 그룹 조회")
    @Test
    void list() {
        MenuGroup menugroup1 = createMenuGroup(LONG_ONE, "순살 치킨");
        MenuGroup menugroup2 = createMenuGroup(LONG_TWO, "오븐 치킨");

        List<MenuGroup> menuGroups = Arrays.asList(menugroup1, menugroup2);

        given(menuGroupDao.findAll()).willReturn(menuGroups);

        List<MenuGroup> findList = menuGroupBo.list();

        assertAll(
                () -> assertThat(findList).containsAll(menuGroups),
                () -> assertThat(findList.size()).isEqualTo(menuGroups.size())
        );
    }

    @DisplayName("메뉴 그룹 추가")
    @Test
    void create() {
        MenuGroup menugroup = createMenuGroup(LONG_ONE, "순살 치킨");

        given(menuGroupDao.save(menugroup)).willReturn(menugroup);

        MenuGroup savedMenuGroup = menuGroupBo.create(menugroup);

        assertAll(
                () -> assertThat(menugroup.getId()).isEqualTo(savedMenuGroup.getId()),
                () -> assertThat(menugroup.getName()).isEqualTo(savedMenuGroup.getName())
        );
    }

    private MenuGroup createMenuGroup(Long menuGroupId, String menuGroupName) {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(menuGroupId);
        menuGroup.setName(menuGroupName);

        return menuGroup;
    }
}
