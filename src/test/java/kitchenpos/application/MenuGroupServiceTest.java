package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
@Sql("/truncate-all.sql")
@SpringBootTest
class MenuGroupServiceTest {
    @Autowired
    private MenuGroupService sut;

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create() {
        final String name = "치킨 세트 그룹";
        final MenuGroup request = new MenuGroup(name);

        final MenuGroup response = sut.create(request);

        assertThat(response.getName()).isEqualTo(name);
    }

    @DisplayName("메뉴 그룹의 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithEmptyName(final String name) {
        final MenuGroup request = new MenuGroup(name);

        assertThatIllegalArgumentException().isThrownBy(
                () -> sut.create(request)
        );
    }

    @DisplayName("메뉴 그룹은 여러개 조회할 수 있다.")
    @Test
    void findAll() {
        final String name = "치킨 세트 그룹";
        sut.create(new MenuGroup(name));

        final List<MenuGroup> response = sut.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo(name);
    }
}
