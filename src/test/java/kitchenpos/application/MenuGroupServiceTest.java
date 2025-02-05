package kitchenpos.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    class Create {

        @DisplayName("메뉴그룹의 이름은 필수요청 값 이다.")
        @Test
        void createWithEmptyName() {
            // given
            MenuGroup menuGroup = TestFixture.createMenuGroup("");

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(menuGroup));
        }

        @DisplayName("메뉴 그룹을 등록 할 수 있다")
        @Test
        void create() {
            // given
            MenuGroup menuGroup = TestFixture.createMenuGroup("menuGroup");
            // when
            MenuGroup savedMenuGroup = menuGroupService.create(menuGroup);
            // then
            assertNotNull(savedMenuGroup);
        }
    }

    @Nested
    class FindAll {

        @BeforeEach
        public void setUp() {
            MenuGroup menuGroup = TestFixture.createMenuGroup("menuGroup");
            menuGroupRepository.save(menuGroup);
            MenuGroup menuGroup1 = TestFixture.createMenuGroup("menuGroup1");
            menuGroupRepository.save(menuGroup1);
        }

        @DisplayName("메뉴그룹을 조회 할 수 있다.")
        @Test
        void findAll() {
            // when
            List<MenuGroup> menuGroups = menuGroupService.findAll();

            // then
            assertThat(menuGroups).hasSize(2);
        }
    }
}
