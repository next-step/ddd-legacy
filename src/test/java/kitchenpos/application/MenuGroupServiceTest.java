package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuGroupServiceTest {

    private InMemoryMenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 null이거나 빈 문자열일 경우 메뉴 그룹 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_null_or_empty_name(String input) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(input);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void create_success() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        String name = "메뉴그룹 이름";
        menuGroup.setName(name);

        // when
        MenuGroup savedGroup = menuGroupService.create(menuGroup);

        // then
        assertThat(savedGroup.getId()).isNotNull();
    }
}