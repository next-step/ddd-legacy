package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
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

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class MenuGroupServiceTest {
    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Nested
    class createTest {
        @DisplayName("메뉴 그룹을 생성할 수 있다.")
        @Test
        void createSuccessTest() {
            final MenuGroup menuGroup = createMenuGroup("메뉴 그룹");

            final MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

            assertThat(createdMenuGroup.getId()).isNotNull();
        }

        @DisplayName("메뉴그룹의 이름이 빈값이거나 null일 경우 예외가 발생한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void createExceptionByNullAndEmptyTest(final String name) {
            final MenuGroup menuGroup = createMenuGroup(name);

            assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("메뉴그룹을 전체 조회할 수 있다.")
        @Test
        void findAllSuccessTest() {
            final MenuGroup createdMenuGroup = menuGroupRepository.save(createMenuGroupWithId("메뉴 그룹"));

            final List<MenuGroup> menuGroups = menuGroupService.findAll();
            final List<UUID> menuGroupIds = menuGroups.stream()
                    .map(MenuGroup::getId)
                    .toList();

            assertAll(
                    () -> assertThat(menuGroups).hasSize(1),
                    () -> assertThat(menuGroupIds).contains(createdMenuGroup.getId())
            );
        }
    }
}
