package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹을 등록한다.")
    @Test
    void create() {
        // given
        MenuGroup menuGroupRequest = createMenuGroupRequest("한마리메뉴");

        // when
        MenuGroup actual = menuGroupService.create(menuGroupRequest);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("한마리메뉴")
        );
    }

    @DisplayName("메뉴 그룹에는 반드시 이름이 있어야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nameIsMandatory(String name) {
        // given when
        MenuGroup menuGroupRequest = createMenuGroupRequest(name);

        // then
        assertThatThrownBy(() -> menuGroupService.create(menuGroupRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public static MenuGroup createMenuGroupRequest(String name) {
        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName(name);
        return menuGroupRequest;
    }

}
