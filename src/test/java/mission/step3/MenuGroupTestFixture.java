package mission.step3;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class MenuGroupTestFixture {
    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createRequestMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}


@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹을 생성한다")
    void create() {
        // given
        MenuGroup request = MenuGroupTestFixture.createRequestMenuGroup("음료");
        MenuGroup expected = MenuGroupTestFixture.createMenuGroup("음료");

        given(menuGroupRepository.save(any(MenuGroup.class)))
                .willReturn(expected);

        // when
        MenuGroup actual = menuGroupService.create(request);

        // then
        assertThat(actual.getName()).isEqualTo("음료");
        verify(menuGroupRepository).save(any(MenuGroup.class));
    }

    @ParameterizedTest
    @DisplayName("메뉴 그룹의 이름이 null 이거나 비어있으면 예외가 발생한다")
    @NullAndEmptySource
    void createWithInvalidName(String invalidName) {
        // given
        MenuGroup request = MenuGroupTestFixture.createRequestMenuGroup(invalidName);

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹의 이름이 비어있으면 예외가 발생한다")
    void createWithEmptyName() {
        // given
        MenuGroup request = MenuGroupTestFixture.createRequestMenuGroup("");

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 메뉴 그룹을 조회한다")
    void findAll() {
        // given
        MenuGroup beverageGroup = MenuGroupTestFixture.createMenuGroup("음료");
        MenuGroup mainGroup = MenuGroupTestFixture.createMenuGroup("메인");

        given(menuGroupRepository.findAll())
                .willReturn(List.of(beverageGroup, mainGroup));

        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertThat(menuGroups).hasSize(2);
        assertThat(menuGroups.get(0).getName()).isEqualTo("음료");
        assertThat(menuGroups.get(1).getName()).isEqualTo("메인");
        verify(menuGroupRepository).findAll();
    }
}
