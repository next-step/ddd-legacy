package kitchenpos.application;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹을 생성한다")
    @Test
    void create() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한식");

        //when
        when(menuGroupRepository.save(any())).thenReturn(menuGroup);
        MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        //then
        assertThat(createdMenuGroup.getName()).isEqualTo(menuGroup.getName());
        then(menuGroupRepository).should(times(1)).save(any());
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
        then(menuGroupRepository).should(never()).save(any());
    }

    @DisplayName("메뉴 그룹을 조회한다")
    @Test
    void getAll() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한식");

        //when
        when(menuGroupRepository.findAll()).thenReturn(List.of(menuGroup));
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertThat(menuGroups.stream().map(MenuGroup::getName)).containsExactly(menuGroup.getName());
        then(menuGroupRepository).should(times(1)).findAll();
    }
}
