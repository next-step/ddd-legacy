package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Sql({"/truncate-all.sql"})
class MenuGroupServiceTest extends IntegrationTest {
    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create() {
        final String name = "치킨 세트 그룹";
        final MenuGroup request = new MenuGroup(name);

        final MenuGroup response = menuGroupService.create(request);

        assertThat(response.getName()).isEqualTo(name);
    }

    @DisplayName("메뉴 그룹의 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithEmptyName(final String name) {
        final MenuGroup request = new MenuGroup(name);

        assertThatIllegalArgumentException().isThrownBy(
                () -> menuGroupService.create(request)
        );
    }

    @DisplayName("메뉴 그룹은 여러개 조회할 수 있다.")
    @Test
    void findAll() {
        final String name = "치킨 세트 그룹";
        menuGroupService.create(new MenuGroup(name));

        final List<MenuGroup> response = menuGroupService.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo(name);
    }
}
