package kitchenpos.bo;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {
    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup menuGroup = new MenuGroup();
    private List<MenuGroup> menuGroupList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        setupMenuGroup(1L, "두마리메뉴");
        List<MenuGroup> menuGroupList = new ArrayList<>();
        menuGroupList.add(this.menuGroup);
        setMenuGroupList(menuGroupList);
    }

    @DisplayName("메뉴그룹을 설정할 수 있다.")
    @Test
    public void setMenuGroup() {
        when(menuGroupDao.save(menuGroup)).thenReturn(menuGroup);

        MenuGroup result = menuGroupBo.create(menuGroup);

        assertThat(result.getName()).isEqualTo("두마리메뉴");
    }

    @DisplayName("메뉴그룹 목록을 볼 수 있다.")
    @Test
    public void listMenuGroup() {
        when(menuGroupDao.findAll()).thenReturn(menuGroupList);

        List<MenuGroup> result = menuGroupBo.list();

        assertThat(result.get(0).getName()).isEqualTo("두마리메뉴");
    }

    private void setMenuGroupList(List<MenuGroup> menuGroupList) {
        this.menuGroupList = menuGroupList;
    }

    private void setupMenuGroup(long id, String name) {
        this.menuGroup.setId(id);
        this.menuGroup.setName(name);
    }
}
