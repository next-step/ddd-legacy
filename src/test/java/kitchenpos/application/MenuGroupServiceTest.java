package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹에는 반드시 이름이 있어야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nameIsMandatory(String name) {
        assertThatThrownBy(() -> createMenuGroup(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName(name);
        return menuGroupService.create(menuGroupRequest);
    }

}
