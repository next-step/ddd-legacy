package kitchenpos.application;


import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.fake.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


@DisplayName("메뉴 그룹 테스트")
class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(new InMemoryMenuGroupRepository());

    }

    @DisplayName("메뉴 그룹을 생성 할때 이름은 필수 이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void name_is_Not_Empty(String name) {
        MenuGroup menuGroup = createMenuGroup(name);

        assertThatIllegalArgumentException().isThrownBy(() ->
                menuGroupService.create(menuGroup)
        );
    }

    @DisplayName("메뉴 그룹을 생성 한다.")
    @Test
    void create() {
        //given
        MenuGroup menuGroup = createMenuGroup("그룹1");

        //when
        final MenuGroup createMenuGroup = menuGroupService.create(menuGroup);

        //then
        assertAll(
                () -> assertThat(createMenuGroup.getName()).isEqualTo(menuGroup.getName()),
                () -> assertThat(createMenuGroup.getId()).isNotNull()
        );
    }

    @DisplayName("메뉴 그룹이 조회 된다.")
    @Test
    void findMenuGroup() {
        //given
        menuGroupService.create(createMenuGroup("세트1"));
        menuGroupService.create(createMenuGroup("세트2"));

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertAll(
                () -> assertThat(menuGroups.size()).isEqualTo(2),
                () -> assertThat(menuGroups).extracting("name").containsExactly("세트1", "세트2")
        );
    }

}
