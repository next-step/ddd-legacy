package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.util.MenuGroupFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create_with_valid_attribute() {
        final String givenMenuGroupName = "test";
        MenuGroup request = new MenuGroup();
        request.setName(givenMenuGroupName);

        MenuGroup actual = menuGroupService.create(request);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(givenMenuGroupName)
        );
    }

    @DisplayName("메뉴 이름이 존재해야한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_with_empty_name(String givenName) {
        MenuGroup request = new MenuGroup();
        request.setName(givenName);

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
        MenuGroup menuGroup1 = MenuGroupFactory.createMenuGroup(givenUUID1, givenName1);
        MenuGroup menuGroup2 = MenuGroupFactory.createMenuGroup(givenUUID2, givenName2);

        menuGroupService = Mockito.mock(MenuGroupService.class);
        given(menuGroupService.findAll()).willReturn(Arrays.asList(menuGroup1, menuGroup2));

        List<MenuGroup> foundMenuGroup = menuGroupService.findAll();

        assertThat(foundMenuGroup).containsAll(Arrays.asList(menuGroup1, menuGroup2));
    }
}
