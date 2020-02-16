package kitchenpos.bo;

import kitchenpos.builder.MenuGroupBuilder;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class MenuGroupBoTestWithBuilder extends MockTest{
    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("메뉴그룹을 설정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"두마리메뉴"})
    public void setMenuGroup(String name) {
        //given
        MenuGroup givenMenuGroup = MenuGroupBuilder.menuGroup()
                .withName(name)
                .build();

        given(menuGroupDao.save(any(MenuGroup.class)))
                .willReturn(givenMenuGroup);

        //when
        MenuGroup actualMenuGroup = menuGroupBo.create(givenMenuGroup);

        //then
        assertThat(actualMenuGroup.getName())
                .isEqualTo(givenMenuGroup.getName());
    }

    @DisplayName("메뉴그룹 목록을 볼 수 있다.")
    @Test
    public void listMenuGroup() {
        //given
        MenuGroup givenMenuGroup = MenuGroupBuilder.menuGroup()
                .build();
        List<MenuGroup> givenMenuGroupList = Arrays.asList(givenMenuGroup);

        given(menuGroupDao.findAll())
                .willReturn(givenMenuGroupList);

        //when
        List<MenuGroup> actualMenuGroupList = menuGroupBo.list();

        //then
        assertThat(actualMenuGroupList.size())
                .isEqualTo(givenMenuGroupList.size());
    }
}
