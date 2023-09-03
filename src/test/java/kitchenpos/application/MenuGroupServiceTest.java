package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fake.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.dummy.DummyMenuGroup.createMenuGroup;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MenuGroupServiceTest {

    MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("[정상] 메뉴 그룹을 등록한다.")
    @Test
    void create() {
        MenuGroup menuGroup = createMenuGroup();
        MenuGroup actual = menuGroupService.create(menuGroup);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertEquals(menuGroup.getName(), actual.getName())
        );
    }

    @DisplayName("[오류] 이름 없음 경우 메뉴 그룹을 등록할 수 없다.")
    @Test
    void create_null_name() {
        MenuGroup menuGroup = createMenuGroup();
        menuGroup.setName(null);
        assertThatThrownBy(
                () -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("[정상] 메뉴 그룹을 조회한다.")
    @Test
    void findAll() {
        menuGroupService.create(createMenuGroup("메뉴그룹1"));
        menuGroupService.create(createMenuGroup("메뉴그룹2"));
        menuGroupService.create(createMenuGroup("메뉴그룹3"));
        assertThat(menuGroupService.findAll().size()).isSameAs(3);
    }

}