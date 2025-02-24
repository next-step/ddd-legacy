package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;

    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setup() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    @DisplayName("메뉴 그룹 생성")
    class CreateGroupMenu {

        @Test
        @DisplayName("메뉴 그룹 생성 성공")
        void testCreateGroupMenu() {
            // given
            final MenuGroup request = MenuGroupFixture.createMenuGroup("한마리메뉴");

            // when
            final MenuGroup result = menuGroupService.create(request);

            // then
            final MenuGroup found = menuGroupRepository.findById(result.getId()).orElse(null);

            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo(request.getName());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("메뉴 그룹은 이름을 필수로 가진다.")
        void testNullOrEmptyName(final String name) {
            // given
            final MenuGroup request = MenuGroupFixture.createMenuGroup(name);

            // when & then
            assertThatException()
                    .isThrownBy(() -> menuGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("그룹 메뉴 조회")
    class FindGroupMenus {

        @Test
        @DisplayName("등록된 모든 메뉴 그룹을 조회한다.")
        void testFindAllGroupMenu() {
            // given
            MenuGroup menuGroup1 = MenuGroupFixture.createMenuGroup("한마리메뉴");
            MenuGroup menuGroup2 = MenuGroupFixture.createMenuGroup("두마리메뉴");
            menuGroupRepository.save(menuGroup1);
            menuGroupRepository.save(menuGroup2);

            // when
            final List<MenuGroup> result = menuGroupService.findAll();

            // then
            assertThat(result)
                    .hasSize(2)
                    .extracting(MenuGroup::getId)
                    .containsExactlyInAnyOrder(menuGroup1.getId(), menuGroup2.getId());
        }
    }

}
