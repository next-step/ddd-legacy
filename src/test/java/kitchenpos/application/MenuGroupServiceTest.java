package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository.deleteAll();
    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroupRepository.save(menuGroup);
    }

    @Nested
    @DisplayName("메뉴 그룹 생성")
    class CreateMenuGroupTest {

        @Test
        @DisplayName("메뉴 그룹을 생성할 수 있다.")
        void create() {
            MenuGroup request = new MenuGroup();
            request.setId(UUID.randomUUID());
            request.setName("한마리메뉴");

            MenuGroup result = menuGroupService.create(request);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getId()).isNotNull();
            Assertions.assertThat(result.getName()).isEqualTo("한마리메뉴");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("메뉴 그룹 생성 시 그룹명이 null 또는 빈 값이면 IllegalArgumentException 예외 발생")
        void cannotCreateMenuGroupWithInvalidName(String invalidName) {
            MenuGroup request = new MenuGroup();
            request.setId(UUID.randomUUID());
            request.setName(invalidName);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuGroupService.create(request));
        }
    }

    @Nested
    @DisplayName("메뉴 그룹 조회")
    class FindMenuGroupTest {

        @Test
        @DisplayName("모든 메뉴 그룹을 조회할 수 있다.")
        void findAll() {
            MenuGroup firstGroup = createMenuGroup("한마리메뉴");
            MenuGroup secondGroup = createMenuGroup("두마리메뉴");

            List<MenuGroup> result = menuGroupService.findAll();

            Assertions.assertThat(result).hasSize(2);
            Assertions.assertThat(result)
                    .extracting(MenuGroup::getName)
                    .containsExactly("한마리메뉴", "두마리메뉴");
        }
    }
}
