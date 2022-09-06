package kitchenpos.integration;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.integration.mock.MemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static kitchenpos.Fixtures.aMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
class MenuGroupServiceTest {

    @Autowired
    MenuGroupService menuGroupService;

    @Autowired
    MemoryMenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository.clear();
    }

    @ParameterizedTest(name = "메뉴 그룹의 이름은 빈 값을 허용하지 않는다. source = {0}")
    @NullAndEmptySource
    void create_IllegalName(String source) {
        MenuGroup request = new MenuGroup(source);

        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        MenuGroup saved = menuGroupService.create(aMenuGroup());

        assertThat(saved.getId()).isNotNull();
    }
}
