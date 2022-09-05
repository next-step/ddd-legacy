package kitchenpos.application;

import kitchenpos.application.support.TestFixture;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹 생성")
    @ParameterizedTest
    @ValueSource(strings = {"menu Group", "proper name", "test name"})
    void create_menu_group(final String name) {
        MenuGroup menuGroup = TestFixture.createFirstMenuGroup();
        menuGroup.setName(name);

        given(menuGroupRepository.save(Mockito.any(MenuGroup.class)))
                .willReturn(menuGroup);

        final MenuGroup result = menuGroupService.create(menuGroup);

        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getId()).isEqualTo(menuGroup.getId());
    }

    @DisplayName("메뉴 그룹의 이름이 null이거나 비어있다면 IllegalArgumentException을 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_menu_group_whit_null_or_empty_name(final String name) {
        final MenuGroup menuGroup = TestFixture.createFirstMenuGroup();
        menuGroup.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("생성된 메뉴 그룹은 조회가 가능하다")
    @Test
    void select_all_menu_group() {
        final MenuGroup firstMenuGroup = TestFixture.createFirstMenuGroup();
        final MenuGroup secondMenuGroup = TestFixture.createSecondMenuGroup();

        final List<MenuGroup> menuGroupList = Arrays.asList(firstMenuGroup, secondMenuGroup);

        given(menuGroupRepository.findAll())
                .willReturn(menuGroupList);

        final List<MenuGroup> result = menuGroupService.findAll();

        assertThat(result).isEqualTo(menuGroupList);
    }
}
