package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MenuGroupServiceTest {
    @Autowired
    private MenuGroupService menuGroupService;

    @Nested
    class createTest {
        @DisplayName("메뉴 그룹을 생성할 수 있다.")
        @Test
        void createSuccessTest() {
            MenuGroup menuGroup = createMenuGroup("메뉴 그룹");

            final MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

            assertThat(createdMenuGroup.getId()).isNotNull();
        }

        @DisplayName("메뉴그룹의 이름이 빈값이거나 null일 경우 예외가 발생한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void createExceptionByNullAndEmptyTest(final String name) {
            MenuGroup menuGroup = createMenuGroup(name);

            assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("메뉴그룹을 전체 조회할 수 있다.")
        @Test
        void findAllSuccessTest() {
            MenuGroup menuGroup = createMenuGroup("메뉴 그룹");

            MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);
            List<MenuGroup> menuGroups = menuGroupService.findAll();
            List<UUID> menuGroupIds = menuGroups.stream()
                    .map(MenuGroup::getId)
                    .toList();

            assertThat(menuGroups).hasSize(1);
            assertThat(menuGroupIds).contains(createdMenuGroup.getId());
        }
    }

    private MenuGroup createMenuGroup(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        return menuGroup;
    }
}
