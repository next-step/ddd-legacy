package kitchenpos;

import kitchenpos.application.InMemoryMenuGroupRepository;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

@DisplayName(value = " Menu 테스트")
public class MenuGroupTest {
    private static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
    private static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName(value = "메뉴 그룹등록 기능")
    @Nested
    class MenuCreateTest {
        @DisplayName(value = "메뉴를 등록할 수 있다.")
        @Test
        void createMenu() {
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            MenuGroup responseMenuGroup = menuGroupService.create(menuGroup);

            assertAll(
                    () -> assertThat(responseMenuGroup.getId()).isNotNull(),
                    () -> assertThat(responseMenuGroup.getName()).isEqualTo(menuGroup.getName()),
                    () -> assertThat(menuGroupRepository.findById(responseMenuGroup.getId())).isNotNull()
            );
        }

        @DisplayName(value = "메뉴그룹의 이름은 없으면 안됩니다.")
        @ParameterizedTest
        @NullAndEmptySource
        void invalidMenuAmount(String menuGroupName) {
            MenuGroup menuGroup = createMenuGroup(menuGroupName, 후라이드치킨_MENU_GROUP_UUID);
            assertThatIllegalArgumentException().isThrownBy(() -> menuGroupService.create(menuGroup));
        }

    }

    @DisplayName(value = "모든 메뉴그룹 조회 기능")
    @Nested
    class findAllMenuGroupTest {
        @DisplayName(value = "모든 메뉴그룹을 조회할 수 있다.")
        @Test
        void findAllMenuGroup() {
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            menuGroupService.create(menuGroup);
            List<MenuGroup> responseMenuGroups = menuGroupService.findAll();

            assertThat(responseMenuGroups.size()).isEqualTo(1);
        }
    }


}
