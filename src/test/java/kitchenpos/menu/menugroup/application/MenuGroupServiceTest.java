package kitchenpos.menu.menugroup.application;

import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.menu.menugroup.dto.request.MenuGroupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@Transactional
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

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> menuGroupService.create(new MenuGroupRequest("메뉴명")));
    }

    @Test
    void name() {
        menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        assertThat(menuGroupService.findAll()).hasSize(1);
    }

    @DisplayName("메뉴 그룹 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        assertThat(menuGroupService.findAll().size()).isEqualTo(1);
    }

}
