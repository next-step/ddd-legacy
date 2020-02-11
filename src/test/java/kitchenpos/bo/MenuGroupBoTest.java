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
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private List<MenuGroup> menuGroups;
    private MenuGroup newMenuGroup;

    @BeforeEach
    void beforeEach() {
        /**
         * 새로운 메뉴그룹
         */
        newMenuGroup = new MenuGroup();
        newMenuGroup.setName("저세상메뉴");

        /**
         * 메뉴그룹 리스트
         */
        menuGroups = new ArrayList<>();

        LongStream.range(0, 100).forEach(i -> {
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(i);
            menuGroup.setName("메뉴그룹" + i);

            menuGroups.add(menuGroup);
        });
    }

    @DisplayName("새로운 메뉴그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(menuGroupDao.save(any(MenuGroup.class))).willAnswer(invocation -> {
            newMenuGroup.setId(1L);
            return newMenuGroup;
        });

        // when
        MenuGroup result = menuGroupBo.create(newMenuGroup);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(newMenuGroup.getName());
    }

    @DisplayName("전체 메뉴그룹 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        given(menuGroupDao.findAll()).willReturn(menuGroups);

        // when
        final List<MenuGroup> result = menuGroupBo.list();

        // then
        assertThat(result.size()).isEqualTo(menuGroups.size());
        assertThat(result.get(0).getId()).isEqualTo(menuGroups.get(0).getId());
        assertThat(result.get(0).getName()).isEqualTo(menuGroups.get(0).getName());
    }
}
