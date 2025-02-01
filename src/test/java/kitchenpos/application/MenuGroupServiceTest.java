package kitchenpos.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
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

        @DisplayName("메뉴 그룹의 이름이 없는 경우 예외를 던진다.")
        @Test
        void createWithEmptyName() {
            // given
            MenuGroup menuGroup = new MenuGroup();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(menuGroup));
        }

        @DisplayName("메뉴 그룹을 생성한다.")
        @Test
        void create() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setName("menuGroup");
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
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setName("menuGroup");
            menuGroup.setId(UUID.randomUUID());
            MenuGroup menuGroup1 = new MenuGroup();
            menuGroup1.setName("menuGroup1");
            menuGroup1.setId(UUID.randomUUID());
            menuGroupRepository.saveAll(List.of(menuGroup, menuGroup1));
        }

        @DisplayName("메뉴 그룹을 조회한다.")
        @Test
        void findAll() {
            // when
            List<MenuGroup> menuGroups = menuGroupService.findAll();

            // then
            assertThat(menuGroups).hasSize(2);
        }
    }
}
