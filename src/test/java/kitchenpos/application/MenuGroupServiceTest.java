package kitchenpos.application;

import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    @DisplayName("메뉴 그룹 생성")
    class CreateMenuGroup {

        @DisplayName("메뉴 그룹명은 비어있으면 안된다.")
        @ParameterizedTest
        @NullAndEmptySource
        void create_name_empty(final String name) {
            // given
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(name);

            //when,then
            assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("새로운 메뉴 그룹을 생성할 수 있다.")
        @ParameterizedTest(name = "메뉴 그룹명 : `{0}`")
        @ValueSource(strings = {"추천 메뉴", "사이드 메뉴", "포장 메뉴"})
        void create(final String name) {
            // given
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(name);

            // when
            MenuGroup actual = menuGroupService.create(menuGroup);
            MenuGroup expect = menuGroupRepository.findById(actual.getId())
                    .orElse(new MenuGroup());

            //then
            assertThat(actual).isEqualTo(expect);
        }
    }

    @DisplayName("메뉴 그룹 전부 가져올 수 있다.")
    @Test
    void findAll() {
        // given
        MenuGroup menuGroup = MenuGroupFixture.createMenuGroup("메뉴그룹");
        menuGroupRepository.save(menuGroup);

        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertEquals(result.size(), 1)
        );
    }
}
