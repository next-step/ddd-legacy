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

import static kitchenpos.fixture.application.MenuGroupFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    private MenuGroup 솔로_메뉴;
    private MenuGroup 커플_메뉴;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        솔로_메뉴 = createMenuGroup("솔로 메뉴");
        커플_메뉴 = createMenuGroup("커플 메뉴");
    }

    @DisplayName("등록")
    @Nested
    class MenuGroupCreate {
        @Test
        @DisplayName("메뉴 그룹을 등록한다.")
        void menuGroupCreate() {
            when(menuGroupRepository.save(any())).thenReturn(솔로_메뉴);

            MenuGroup menuGroup = menuGroupService.create(솔로_메뉴);

            assertThat(menuGroup.getId()).isEqualTo(솔로_메뉴.getId());
        }

        @ParameterizedTest
        @DisplayName("메뉴 그룹의 이름이 null이거나 공백이면 IllegalException을 반환한다.")
        @NullAndEmptySource
        void menuGroupNameIsNotNull(String name) {
            MenuGroup request = createMenuGroup(name);

            assertThatThrownBy(() -> menuGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Test
    @DisplayName("메뉴 그룹 목록을 볼 수 있다.")
    void menuGroups() {
        List<MenuGroup> menuGroups = List.of(솔로_메뉴, 커플_메뉴);
        when(menuGroupRepository.findAll()).thenReturn(menuGroups);

        List<MenuGroup> result = menuGroupService.findAll();

        assertThat(result).containsExactly(솔로_메뉴, 커플_메뉴);
    }
}
