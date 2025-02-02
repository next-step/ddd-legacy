package kitchenpos;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName(value = " Menu 테스트")
@Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MenuGroupTest {
    private static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
    private static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";

    @SpyBean
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName(value = "메뉴 그룹등록 기능")
    @Nested
    class MenuCreateTest {
        @DisplayName(value = "메뉴를 등록할 수 있다.")
        @Test
        void createMenu() {
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            MenuGroup responseMenuGroup = menuGroupService.create(menuGroup);
            //행위검증
            verify(menuGroupRepository, times(1)).save(Mockito.any());

            assertAll(
                    () -> assertThat(responseMenuGroup.getId()).isNotNull(),
                    () -> assertThat(responseMenuGroup.getName()).isEqualTo(menuGroup.getName())
            );
        }

        @DisplayName(value = "메뉴그룹의 이름은 없으면 안됩니다.")
        @Test
        void invalidMenuAmount() {
            MenuGroup menuGroup = createMenuGroup("", 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuGroupService.create(menuGroup);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

    }

    @DisplayName(value = "모든 메뉴그룹 조회 기능")
    @Nested
    class findAllMenuGroupTest {
        @DisplayName(value = "모든 메뉴그룹을 조회할 수 있다.")
        @Test
        void createMenu() {
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            menuGroupService.create(menuGroup);
           List<MenuGroup> responseMenuGroups = menuGroupService.findAll();
            //행위검증
            verify(menuGroupRepository, times(1)).findAll();
            assertThat(responseMenuGroups.size()).isEqualTo(1);

        }
    }

    private static MenuGroup createMenuGroup(final String name, final UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }

}
