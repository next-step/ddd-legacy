package kitchenpos.application;

import kitchenpos.application.fixture.MenuGroupFixture;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹을 생성할 수 있다")
    @Nested
    class MenuGroupCreator {

        @DisplayName("메뉴그룹명을 입력하여 메뉴그룹을 등록한다")
        @Test
        void createMenuGroup() {
            MenuGroup request = MenuGroupFixture.createMenuGroup("세트메뉴");

            MenuGroup menuGroup = menuGroupService.create(request);

            assertThat(menuGroup.getName()).isEqualTo("세트메뉴");
        }

        @DisplayName("메뉴그룹명은 반드시 입력되어야 하며 공백만 입력할 수 없다")
        @NullAndEmptySource
        @ParameterizedTest
        void validateName(String name) {
            MenuGroup request = MenuGroupFixture.createMenuGroup(name);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuGroupService.create(request));
        }
    }


    @DisplayName("모든 메뉴 그룹을 조회할 수 있다")
    @Test
    void findAll() {
        menuGroupService.create(MenuGroupFixture.createMenuGroup("버거"));
        menuGroupService.create(MenuGroupFixture.createMenuGroup("세트메뉴"));
        menuGroupService.create(MenuGroupFixture.createMenuGroup("사이드"));

        List<MenuGroup> menuGroups = menuGroupService.findAll();

        assertAll(
                () -> assertThat(menuGroups.size()).isEqualTo(3),
                () -> assertThat(menuGroups).extracting(MenuGroup::getName)
                        .contains("버거", "세트메뉴", "사이드")
        );

    }
}
