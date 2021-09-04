package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuGroupServiceTest {
    private MenuGroupService menuGroupService = new MenuGroupService(MenuGroupFixture.menuGroupRepository);

    @AfterEach
    void cleanUp() {
        MenuGroupFixture.비우기();
    }

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create() {
        final MenuGroup menuGroup = MenuGroupFixture.메뉴그룹();
        final MenuGroup saved = 메뉴그룹등록(menuGroup);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(menuGroup.getName())
        );
    }

    @DisplayName("메뉴 그룹의 이름은 빈 값이 아니어야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        final MenuGroup menuGroup = MenuGroupFixture.메뉴그룹();
        menuGroup.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 메뉴그룹등록(menuGroup));
    }

    @DisplayName("메뉴 그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        final MenuGroup saved1 = MenuGroupFixture.메뉴그룹저장();
        final MenuGroup saved2 = MenuGroupFixture.메뉴그룹저장();

        assertThat(메뉴그룹조회()).containsOnly(saved1, saved2);
    }

    private MenuGroup 메뉴그룹등록(final MenuGroup menuGroup) {
        return menuGroupService.create(menuGroup);
    }

    private List<MenuGroup> 메뉴그룹조회() {
        return menuGroupService.findAll();
    }
}
