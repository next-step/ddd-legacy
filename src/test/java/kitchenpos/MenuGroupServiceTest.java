package kitchenpos;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static kitchenpos.TestFixtureFactory.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("유효한 메뉴 그룹 생성 및 반환")
    void createValidMenuGroupReturnsCreatedMenuGroup() {
        // Arrange
        MenuGroup 한마리_메뉴그룹 = createMenuGroup("한마리 메뉴");
        when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(한마리_메뉴그룹);

        MenuGroup request = createMenuGroup("한마리 메뉴");

        // Act
        MenuGroup createdMenuGroup = menuGroupService.create(request);

        // Assert
        assertThat(createdMenuGroup).isNotNull();
        assertThat(createdMenuGroup.getName()).isEqualTo(request.getName());
        verify(menuGroupRepository, times(1)).save(any(MenuGroup.class));
    }

    @Test
    @DisplayName("메뉴 그룹명이 없는 경우 IllegalArgumentException 발생")
    void createNullNameThrowsIllegalArgumentException() {
        // Arrange
        MenuGroup request = createMenuGroup(null);

        // Act & Assert
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(menuGroupRepository, never()).save(any(MenuGroup.class));
    }

    @Test
    @DisplayName("모든 메뉴 그룹 조회 시 리스트 반환")
    void findAllReturnsListOfMenuGroups() {
        // Arrange
        List<MenuGroup> menuGroups = new ArrayList<>();
        menuGroups.add(createMenuGroup("한마리 메뉴"));
        menuGroups.add(createMenuGroup("두마리 메뉴"));

        when(menuGroupRepository.findAll()).thenReturn(menuGroups);

        // Act
        List<MenuGroup> result = menuGroupService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(menuGroupRepository, times(1)).findAll();
    }
}
