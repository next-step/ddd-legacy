package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("메뉴그룹을 생성할 수 있다.")
    @Test
    void create() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("name");

        when(menuGroupDao.save(menuGroup)).thenReturn(menuGroup);
        assertThat(menuGroupBo.create(menuGroup)).isEqualTo(menuGroup);
    }

    @DisplayName("메뉴그룹 목록을 조회할 수 있다.")
    @Test
    void list() {
        when(menuGroupDao.findAll()).thenReturn(new ArrayList<>());
        assertThat(menuGroupBo.list()).isEmpty();
    }
}
