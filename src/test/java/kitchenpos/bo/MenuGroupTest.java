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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuGroupTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        this.menuGroup = new MenuGroup(1L, "추천메뉴");
    }

    @Test
    @DisplayName("메뉴 그룹(카테고리) 등록")
    void createMenuGroup() {
        // give
        given(menuGroupDao.save(menuGroup))
                .willReturn(menuGroup);
        // when
        MenuGroup menuGroupActual = menuGroupBo.create(menuGroup);
        MenuGroup menuGroupExpected = menuGroup;
        // then
        assertThat(menuGroupActual.getId()).isEqualTo(menuGroupExpected.getId());
    }

    @Test
    @DisplayName("등록되어진 메뉴 그룹을 볼 수 있다.")
    void getMenuGroup() {
        // give
        given(menuGroupDao.findAll())
                .willReturn(
                        Arrays.asList(
                                new MenuGroup(1L, "추천 메뉴"),
                                new MenuGroup(2L, "이번달 메뉴")));
        // when
        List<MenuGroup> menuGroupsActual = menuGroupBo.list();
        // then
        assertThat(menuGroupsActual.size()).isEqualTo(2);
        assertThat(menuGroupsActual.contains(new MenuGroup(1L, "추천 메뉴"))).isFalse();
    }
}
