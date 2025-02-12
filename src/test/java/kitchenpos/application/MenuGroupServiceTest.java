package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository = mock(MenuGroupRepository.class);
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다")
    void create_menuGroup() {
        // given
        MenuGroup request = new MenuGroup("한식");

        when(menuGroupRepository.save(any(MenuGroup.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MenuGroup result = menuGroupService.create(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("한식");
        verify(menuGroupRepository).save(any(MenuGroup.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 그룹 이름이 null이거나 비어있으면 예외가 발생한다")
    void create_MenuGroup_fail(String name) {
        // given
        MenuGroup request = new MenuGroup(name);

        // when // then
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹 목록을 조회한다")
    void findAll() {
        // given
        MenuGroup group1 = new MenuGroup("한식");
        MenuGroup group2 = new MenuGroup("중식");
        List<MenuGroup> expected = Arrays.asList(group1, group2);
        when(menuGroupRepository.findAll()).thenReturn(expected);

        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        assertThat(result).hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(expected);
        verify(menuGroupRepository).findAll();
    }
}
