package kitchenpos.bo;

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
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest extends Fixtures {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @Test
    @DisplayName("메뉴 그룹을 등록 할 수 있다.")
    void create() {
        final MenuGroup menuGroup = menuGroups.get(0);

        given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

        final MenuGroup savedMenuGroup = menuGroupBo.create(menuGroup);

        assertThat(savedMenuGroup).isNotNull();
        assertThat(savedMenuGroup.getId()).isEqualTo(menuGroup.getId());
    }

    @Test
    @DisplayName("메뉴 그룹 목록을 조회 할 수 있다.")
    void list() {

        final List<MenuGroup> menuGroupList = menuGroups;
        given(menuGroupDao.findAll()).willReturn(menuGroupList);

        final List<MenuGroup> result = menuGroupBo.list();

        assertThat(menuGroupList).isNotEmpty();
        assertThat(menuGroupList.size()).isEqualTo(result.size());
    }
}