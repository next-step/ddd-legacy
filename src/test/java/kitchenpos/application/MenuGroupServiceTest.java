package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    private final static UUID MENU_GROUP_FIRST_ID = UUID.randomUUID();
    private final static UUID MENU_GROUP_SECOND_ID = UUID.randomUUID();

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private MenuGroup createMenuGroup(final UUID ID, final String name) {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(ID);
        menuGroup.setName(name);

        return menuGroup;
    }

    @DisplayName("메뉴 그룹 생성")
    @ParameterizedTest
    @ValueSource(strings = {"menu Group", "proper name", "test name"})
    void create_menu_group(final String name) {
        final MenuGroup menuGroup = createMenuGroup(MENU_GROUP_FIRST_ID, name);

        given(menuGroupRepository.save(Mockito.any(MenuGroup.class)))
                .willReturn(menuGroup);

        final MenuGroup result = menuGroupService.create(menuGroup);

        assertThat(result.getName()).isEqualTo(menuGroup.getName());
        assertThat(result.getId()).isEqualTo(menuGroup.getId());
    }

    @DisplayName("메뉴 그룹의 이름은 필수이다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_menu_group_whit_null_or_empty_name(final String name) {
        final MenuGroup menuGroup = createMenuGroup(MENU_GROUP_FIRST_ID, name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("생성된 메뉴 그룹은 조회가 가능하다")
    @Test
    void select_all_menu_group() {
        final MenuGroup chickenGroup = createMenuGroup(MENU_GROUP_FIRST_ID, "치킨");
        final MenuGroup beerGroup = createMenuGroup(MENU_GROUP_SECOND_ID, "맥주");

        final List<MenuGroup> menuGroupList = Arrays.asList(chickenGroup, beerGroup);

        given(menuGroupRepository.findAll())
                .willReturn(menuGroupList);

        final List<MenuGroup> result = menuGroupService.findAll();

        assertThat(result).isEqualTo(menuGroupList);
    }
}