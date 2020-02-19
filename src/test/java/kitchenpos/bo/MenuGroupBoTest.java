package kitchenpos.bo;

import kitchenpos.Fixtures;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    MenuGroupDao menuGroupDao;

    @InjectMocks
    MenuGroupBo menuGroupBo;

    private MenuGroup defaultMenuGroup;
    private List<MenuGroup> defaultMenuGroups;

    @BeforeEach
    public void setUp() {
        defaultMenuGroup = Fixtures.getMenuGroup(1L, "신메뉴");
        defaultMenuGroups = getDefaultMenuGroups();
    }

    @DisplayName("메뉴그룹의 정상값으로 메뉴그룹 생성 시 성공확인")
    @Test
    public void createNormal() {
        given(menuGroupDao.save(defaultMenuGroup)).willReturn(defaultMenuGroup);
        MenuGroup menuGroupCreated = menuGroupBo.create(defaultMenuGroup);
        assertThat(menuGroupCreated).isEqualTo(defaultMenuGroup);
    }

    @DisplayName("메뉴그룹을 성공적으로 조회되는 지 확인")
    @Test
    public void list() {
        given(menuGroupDao.findAll()).willReturn(defaultMenuGroups);
        assertThat(menuGroupBo.list()).contains(defaultMenuGroups.toArray(new MenuGroup[0]));
    }

    private List<MenuGroup> getDefaultMenuGroups() {
        ArrayList<MenuGroup> menuGroups = new ArrayList<>();
        menuGroups.add(Fixtures.getMenuGroup(1L, "신메뉴1"));
        menuGroups.add(Fixtures.getMenuGroup(2L, "신메뉴2"));
        menuGroups.add(Fixtures.getMenuGroup(3L, "신메뉴3"));
        return menuGroups;
    }


}