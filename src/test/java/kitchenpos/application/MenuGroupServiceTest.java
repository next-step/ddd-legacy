package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.StubMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class MenuGroupServiceTest {
    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new StubMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create_with_valid_attribute() {
        final String givenMenuGroupName = "test";
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        MenuGroup request = createMenuGroup(givenUUID, givenMenuGroupName);

        MenuGroup actual = menuGroupService.create(request);

        assertThat(actual).isNotNull();
    }

    @DisplayName("메뉴 이름이 존재해야한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_with_empty_name(String givenName) {
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        MenuGroup request = createMenuGroup(givenUUID, givenName);

        assertThatCode(() ->
                menuGroupService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 목록을 조회할 수 있다")
    @Test
    void get_menu_groups() {
        final UUID givenUUID1 = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final UUID givenUUID2 = UUID.fromString("b619cf4e-3725-48b3-9e32-84eb2e92e5b9");
        final String givenName1 = "test1";
        final String givenName2 = "test2";
        MenuGroup request1 = createMenuGroup(givenUUID1, givenName1);
        MenuGroup request2 = createMenuGroup(givenUUID2, givenName2);
        menuGroupRepository.save(request1);
        menuGroupRepository.save(request2);

        List<MenuGroup> foundMenuGroup = menuGroupService.findAll();

        assertThat(foundMenuGroup).containsExactly(request1, request2);
    }

    private MenuGroup createMenuGroup(UUID uuid, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(uuid);
        menuGroup.setName(name);
        return menuGroup;
    }
}
