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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup expectedMenuGroup = null;

    @BeforeEach
    void setUp() {
        expectedMenuGroup = new MenuGroup();
        expectedMenuGroup.setName("menuGroup");
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void createMenuGroup() {
        given(menuGroupDao.save(any(MenuGroup.class))).willReturn(expectedMenuGroup);

        MenuGroup actual = menuGroupBo.create(expectedMenuGroup);

        assertThat(actual.getName()).isEqualTo(expectedMenuGroup.getName());
    }

    @DisplayName("메뉴 그룹 목록을 확인한다.")
    @Test
    void getMenuGroups() {
        //given
        expectedMenuGroup.setId(1L);
        given(menuGroupDao.findAll()).willReturn(Arrays.asList(expectedMenuGroup));
        //when
        List<MenuGroup> actual = menuGroupBo.list();

        //then
        assertThat(actual).isEqualTo(menuGroupDao.findAll());
    }
}
