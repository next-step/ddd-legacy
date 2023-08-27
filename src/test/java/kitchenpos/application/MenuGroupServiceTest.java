package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuGroup")
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Nested
    @DisplayName("메뉴그룹을 등록할 수 있다.")
    class create {

        @Test
        @DisplayName("등록")
        void create_1() {
            // Given
            MenuGroup menuGroup = createMenuGroup();
            when(menuGroupRepository.save(any())).thenReturn(menuGroup);

            // When
            MenuGroup result = menuGroupService.create(menuGroup);

            // Then
            assertThat(result).isEqualTo(menuGroup);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("이름은 비어있거나 공백이면 예외가 발생한다.")
        void create_2(String name) {
            // When
            MenuGroup menuGroup = createMenuGroupWithName(name);

            // Then
            assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("메뉴그룹 전체 목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<MenuGroup> menuGroups = createMenuGroups();
        when(menuGroupRepository.findAll()).thenReturn(menuGroups);

        // When
        List<MenuGroup> findAllMenuGroups = menuGroupService.findAll();

        // Then
        assertThat(findAllMenuGroups).hasSize(menuGroups.size());
    }

}