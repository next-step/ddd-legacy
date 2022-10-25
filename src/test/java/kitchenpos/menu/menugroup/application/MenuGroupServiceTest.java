package kitchenpos.menu.menugroup.application;

import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.menu.menugroup.dto.request.MenuGroupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("메뉴 그룹 서비스")
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository, purgomalumClient);
    }

    @Test
    void name() {
        menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        assertThat(menuGroupService.findAll()).hasSize(1);
    }

    @DisplayName("메뉴명은 null 또는 공백일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menuName(String name) {
        assertThatThrownBy(() -> menuGroupService.create(new MenuGroupRequest(name)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }

}
