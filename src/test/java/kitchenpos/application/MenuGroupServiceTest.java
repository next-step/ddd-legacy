package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("MenuGroup 은")
class MenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @DisplayName("추가할 수 있다.")
    @Nested
    class 추가할_수_있다 {

        @Test
        @DisplayName("이름이 있는 경우 추가 할 수 있다.")
        void 이름이_있는_경우_추가_할_수_있다() {
            final MenuGroup menuGroup = createMenuGroup("test");

            doReturn(menuGroup).when(menuGroupRepository).save(any());

            final MenuGroup actual = menuGroupService.create(menuGroup);
            assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menuGroup.getName())
            );
        }

        @ParameterizedTest(name = "{0} 인 경우 추가 할 수 없다.")
        @DisplayName("이름이")
        @NullAndEmptySource
        void 이름이_공백_혹은_없다면_추가_할_수_없다(String name) {
            final MenuGroup menuGroup = createMenuGroup(name);

            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuGroupService.create(menuGroup));
        }
    }

    private static MenuGroup createMenuGroup(UUID id, String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    private static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(null, name);
    }
}
