package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.TestMenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MenuGroupBoTest {

    private MenuGroupDao menuGroupDao = new TestMenuGroupDao();

    private MenuGroupBo menuGroupBo;

    @BeforeEach
    void setUp() {
        menuGroupBo = new MenuGroupBo(menuGroupDao);
    }

    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup expected = new MenuGroup() {{
            setId(1L);
            setName("기본 메뉴 그룹");
        }};

        // when
        final MenuGroup actual = menuGroupBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @DisplayName("메뉴 그룹 목록을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final MenuGroup expected = new MenuGroup() {{
            setId(1L);
            setName("기본 메뉴 그룹");
        }};
        menuGroupDao.save(expected);

        // when
        final List<MenuGroup> actual = menuGroupBo.list();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).contains(expected);
    }
}
