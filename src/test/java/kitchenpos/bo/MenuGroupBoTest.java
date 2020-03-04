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
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup expected;

    @BeforeEach
    void setUp() {
        menuGroupBo = new MenuGroupBo(menuGroupDao);
        expected = new MenuGroup();
        expected.setId(1L);
        expected.setName("두마리메뉴");
    }

    @DisplayName("메뉴그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(menuGroupDao.save(any(MenuGroup.class))).willReturn(expected);

        // when
        final MenuGroup actual = menuGroupBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @DisplayName("메뉴그룹을 검색할 수 있다.")
    @Test
    void list() {
        // given
        given(menuGroupDao.findAll())
                .willReturn(Collections.singletonList(expected));

        // when
        List<MenuGroup> menuGroupList = menuGroupBo.list();
        MenuGroup menuGroup = menuGroupList.get(0);

        // then
        assertThat(menuGroup).isNotNull();
        assertThat(menuGroup.getName()).isEqualTo(expected.getName());
    }
}
