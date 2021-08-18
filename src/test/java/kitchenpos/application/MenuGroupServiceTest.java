package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;
    
    @DisplayName("새로운 메뉴 그룹을 추가할 수 있다.")
    @ValueSource(strings = {"메뉴 그룹 이름"})
    @ParameterizedTest
    void create(final String expectedName) {
        // given
        final MenuGroup menuGroup = createMenuGroup(expectedName);
        given(menuGroupRepository.save(any(MenuGroup.class)))
                .willReturn(menuGroup);

        // when
        final MenuGroup actual = menuGroupService.create(menuGroup);

        // then
        assertThat(actual.getName())
                .isEqualTo(expectedName);
    }

    @DisplayName("이름이 없는 메뉴그룹은 추가할 수 없다.")
    @Test
    void create_emptyName() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(new MenuGroup()));
    }

    @DisplayName("메뉴 그룹의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final List<MenuGroup> expectedMenuGroups = Arrays.asList("첫번째 메뉴 그룹", "두번째 메뉴 그룹").stream()
                .map(this::createMenuGroup)
                .collect(Collectors.toList());
        given(menuGroupRepository.findAll())
                .willReturn(expectedMenuGroups);

        // when
        final List<MenuGroup> actual = menuGroupService.findAll();

        // then
        assertThat(actual)
                .isEqualTo(expectedMenuGroups);
    }

    private MenuGroup createMenuGroup(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }
}
