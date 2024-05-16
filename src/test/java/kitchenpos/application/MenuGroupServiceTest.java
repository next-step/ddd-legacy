package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("`메뉴 그룹`을 생성할 수 있다.")
    @Test
    void createMenuGroupWithValidInput() {
        // given
        MenuGroup validMenuGroup = new MenuGroup();
        validMenuGroup.setName("메뉴 그룹 이름");

        // when
        when(menuGroupRepository.save(any())).then(invocation -> invocation.getArgument(0));

        var menuGroup = menuGroupService.create(validMenuGroup);

        // then
        assertAll(
            () -> assertThat(menuGroup).isNotNull(),
            () -> assertThat(menuGroup.getName()).isEqualTo(validMenuGroup.getName()),
            () -> assertThat(menuGroup.getId()).isNotNull()
        );
    }

    @DisplayName("`메뉴 그룹`의 이름은 비어있을 수 없다.")
    @Test
    void createMenuGroupWithEmptyName() {
        // given
        MenuGroup invalidMenuGroup = new MenuGroup();
        invalidMenuGroup.setName("");

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(invalidMenuGroup))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`메뉴 그룹`의 이름은 null일 수 없다.")
    @Test
    void createMenuGroupWithNullName() {
        // given
        MenuGroup invalidMenuGroup = new MenuGroup();
        invalidMenuGroup.setName(null);

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(invalidMenuGroup))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`메뉴 그룹`을 조회할 수 있다.")
    @Test
    void findAllMenuGroups() {
        // given
        when(menuGroupRepository.findAll()).thenReturn(List.of(new MenuGroup()));

        // when
        var menuGroups = menuGroupService.findAll();

        // then
        assertThat(menuGroups).isNotEmpty();
    }
}
