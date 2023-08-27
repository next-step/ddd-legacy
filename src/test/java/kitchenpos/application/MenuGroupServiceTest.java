package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuGroup")
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private static final UUID MENU_GROUP_ID = UUID.randomUUID();
    private static final String MENU_GROUP_NAME = "name";
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName(MENU_GROUP_NAME);
    }

    @Nested
    @DisplayName("메뉴그룹을 등록할 수 있다.")
    class create {

        @Test
        @DisplayName("등록")
        void create_1() {
            // Given
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
            menuGroup.setName(name);

            // Then
            assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("메뉴그룹 전체 목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<MenuGroup> menuGroups = List.of(menuGroup, menuGroup);
        when(menuGroupRepository.findAll()).thenReturn(menuGroups);

        // When
        List<MenuGroup> findAllMenuGroups = menuGroupService.findAll();

        // Then
        assertThat(findAllMenuGroups.size()).isEqualTo(2);
    }

}