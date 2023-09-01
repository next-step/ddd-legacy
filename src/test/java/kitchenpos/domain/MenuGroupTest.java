package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MenuGroupTest {

    @Test
    @DisplayName("메뉴그룹은 이름과 식별자를 가진다.")
    void create() {
        UUID id = UUID.randomUUID();

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName("치킨 메뉴그룹");

        assertThat(menuGroup.getId()).isEqualTo(id);
        assertThat(menuGroup.getName()).isEqualTo("치킨 메뉴그룹");
    }
}
