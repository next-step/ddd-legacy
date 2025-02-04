package kitchenpos.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("MenuGroupService 클래스의")
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("create 메소드는")
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

    @DisplayName("findAll 메소드는")
    @Nested
    class FindAll {

        @BeforeEach
        public void setUp() {
            MenuGroup menuGroup = TestFixture.createMenuGroup("menuGroup");
            MenuGroup menuGroup1 = TestFixture.createMenuGroup("menuGroup1");
            menuGroupRepository.saveAll(List.of(menuGroup, menuGroup1));
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
