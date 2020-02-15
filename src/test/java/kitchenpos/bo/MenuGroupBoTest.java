package kitchenpos.bo;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.assertj.core.api.Assertions;
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

    @DisplayName("메뉴 그룹을 등록할수있다")
    @Test
    void create() {
        //given
        MenuGroup menuGroup = createMenuGroup();
        given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

        //when, then
        Assertions.assertThat(menuGroupBo.create(menuGroup)).isEqualTo(menuGroup);
    }

    @DisplayName("전체 메뉴 그룹 목록을 조회할수 있다.")
    @Test
    void list() {
        //given
        List<MenuGroup> groups = Arrays
            .asList(createMenuGroup(), createMenuGroup(), createMenuGroup());

        given(menuGroupDao.findAll()).willReturn(groups);

        //when then
        Assertions.assertThat(menuGroupBo.list()).containsAll(groups);
    }

    private MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("치킨_" + System.currentTimeMillis());
        return menuGroup;
    }
}
