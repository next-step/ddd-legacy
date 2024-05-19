package kitchenpos.application;

import fixtures.MenuGroupBuilder;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹을 생성한다")
    void createMenuGroupTest() {

        MenuGroup menuGroup = new MenuGroupBuilder()
                .withName("한식")
                .build();

        MenuGroup created = menuGroupService.create(menuGroup);

        assertThat(created).isNotNull();
    }

    @DisplayName("메뉴 그룹의 이름은 1글자 이상만 허용된다")
    @ParameterizedTest
    @NullAndEmptySource
    void createMenuGroupWithInvalidNameTest(String name) {

        MenuGroup menuGroup = new MenuGroupBuilder()
                .withName(name)
                .build();

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
