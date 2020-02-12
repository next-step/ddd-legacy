package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.mock.MenuGroupBuilder;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
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

    @DisplayName("새로운 메뉴그룹을 생성할 수 있다.")
    @Test
    void create() {
        MenuGroup newMenuGroup = MenuGroupBuilder.mock()
                .name("저세상메뉴")
                .build();

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
        MenuGroup menuGroup1 = MenuGroupBuilder.mock()
                .id(1L)
                .name("메뉴그룹1")
                .build();
        MenuGroup menuGroup2 = MenuGroupBuilder.mock()
                .id(2L)
                .name("메뉴그룹2")
                .build();

        given(menuGroupDao.findAll()).willReturn(Arrays.asList(menuGroup1, menuGroup2));

        // when
        final List<MenuGroup> result = menuGroupBo.list();

        // then
        assertThat(result).containsExactlyInAnyOrder(menuGroup1, menuGroup2);
    }
}
