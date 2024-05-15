package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.MenuGroup;
import kitchenpos.helper.MenuGroupTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Transactional
class MenuGroupServiceTest extends SetupTest{
    @Autowired
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @DisplayName("메뉴 카테고리를 추가한다.")
    @Test
    void createMenuGroup(){
        //given
        MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setName("추천메뉴");

        //when
        MenuGroup createMenuGroup = menuGroupService.create(requestMenuGroup);

        //then
        assertThat(createMenuGroup.getName()).isSameAs(requestMenuGroup.getName());
    }

    @DisplayName("모든 메뉴 카테고리를 조회한다.")
    @Test
    void getAllMenuGroup(){
        //given
        MenuGroup menuGroup1 = MenuGroupTestHelper.메뉴카테고리_생성("추천메뉴");
        MenuGroup menuGroup2 = MenuGroupTestHelper.메뉴카테고리_생성("원플러스원메뉴");

        List<MenuGroup> menuGroups = Arrays.asList(menuGroup1, menuGroup2);

        //when
        List<MenuGroup> menuGroupList = menuGroupService.findAll();
        //then
        assertThat(menuGroupList.size()).isSameAs(menuGroups.size());
    }

    @DisplayName("메뉴 카테고리 이름을 빈값을 전달하는 경우 IllegalArgumentException 예외가 발생한다. ")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrnullMenuGroupName(String name){
        MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(requestMenuGroup));
    }
}
