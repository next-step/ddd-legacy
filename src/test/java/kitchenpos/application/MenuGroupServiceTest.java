package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹을 생성한다")
    @Test
    void create() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한식");

        //when
        MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        //then
        assertThat(createdMenuGroup.getName()).isEqualTo(menuGroup.getName());
    }

    @DisplayName("메뉴 그룹 생성시 이름이 null 혹은 빈 값이면 생성을 실패한다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_exception(String name) {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("메뉴 그룹을 조회한다")
    @Test
    void getAll() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한식");
        MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        //when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertThat(menuGroups.stream().map(MenuGroup::getName)).containsExactly(createdMenuGroup.getName());
    }
}
