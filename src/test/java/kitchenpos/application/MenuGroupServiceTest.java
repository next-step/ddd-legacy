package kitchenpos.application;

import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹 생성 이름 필수")
    @Test
    public void 메뉴그룹생성_name() throws Exception {
        assertThatThrownBy(
                () -> menuGroupService.create(new MenuGroup())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 생성 Null 체크")
    @ParameterizedTest
    @NullSource
    public void 메뉴그룹생성_name_null(String name) {
        assertThrows(IllegalArgumentException.class, () -> {
            menuGroupService.create(MenuGroupFixture.create(name));
        });
    }

    @DisplayName("메뉴 그룹 생성조회")
    @Test
    public void 메뉴그룹_생성조회() {
        menuGroupService.create(MenuGroupFixture.create("한식"));
        List<MenuGroup> menuGroups = menuGroupService.findAll();
        assertThat(menuGroups.get(0).getName()).isEqualTo("한식");
    }


}