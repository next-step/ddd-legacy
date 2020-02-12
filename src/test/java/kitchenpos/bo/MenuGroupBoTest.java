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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    private static final String MENU_GROUP_NAME = "신메뉴";

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup input;
    private MenuGroup saved;

    @BeforeEach
    void setUp() {
        input = new MenuGroup();
        input.setName(MENU_GROUP_NAME);

        saved = new MenuGroup();
        saved.setName(MENU_GROUP_NAME);
        saved.setId(1L);
    }

    @DisplayName("메뉴 그룹 저장")
    @Test
    void create() {
        given(menuGroupDao.save(input))
                .willReturn(saved);

        MenuGroup result = menuGroupBo.create(input);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo(MENU_GROUP_NAME);
    }

    @DisplayName("메뉴 그룹 조회")
    @Test
    void list() {
        given(menuGroupDao.findAll())
                .willReturn(Collections.singletonList(saved));

        List<MenuGroup> result = menuGroupBo.list();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(MENU_GROUP_NAME);
    }
}
