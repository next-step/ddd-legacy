package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MenuGroupBoTest extends MockTest {
    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("메뉴그룹을 등록할 수 있다")
    @Test
    void createMenuGroup() {
        MenuGroup expected = TestFixtures.menuGroup();

        //given
        given(menuGroupDao.save(expected)).willReturn(expected);

        //when
        MenuGroup result = menuGroupBo.create(expected);

        //then
        assertThat(result.getName()).isEqualTo(expected.getName());
    }

    @DisplayName("메뉴그룹을 조회할 수 있다")
    @Test
    void listMenuGroup() {
        List<MenuGroup> expected = new ArrayList<>();
        MenuGroup menuGroup = TestFixtures.menuGroup();
        expected.add(menuGroup);

        //given
        given(menuGroupDao.findAll()).willReturn(expected);

        //when
        List<MenuGroup> result = menuGroupBo.list();

        //then
        assertThat(result.size()).isEqualTo(1);
    }
}
