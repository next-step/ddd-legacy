package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class MenuGroupServiceTest extends ObjectCreator {
    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new TestMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);

        saveInitialData();
    }

    private void saveInitialData() {
        Arrays.asList("메뉴1", "메뉴2")
                .forEach(menuName -> menuGroupRepository.save(createMenuGroup(menuName)));
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void create() {
        String name = "메뉴1";
        MenuGroup request = createMenuGroup(name);

        MenuGroup menuGroup = menuGroupService.create(request);

        assertAll(
                () -> assertNotNull(menuGroup.getId()),
                () -> assertEquals(menuGroup.getName(), name)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 그룹명은 비어있으면 안된다.")
    void create_valid_name(String name) {
        MenuGroup request = createMenuGroup(name);

        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴그룹을 전체 조회한다.")
    void findAll() {
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        assertThat(menuGroups).hasSize(2);
    }
}
