package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuGroupServiceIntegrationTest extends IntegrationTest {

    private final MenuGroupService menuGroupService;
    private final MenuGroupRepository menuGroupRepository;

    MenuGroupServiceIntegrationTest(MenuGroupRepository menuGroupRepository, MenuGroupService menuGroupService) {
        this.menuGroupRepository = menuGroupRepository;
        this.menuGroupService = menuGroupService;
    }

    @DisplayName("[정상] 메뉴 그룹이 정상적으로 등록됩니다.")
    @Test
    void create_success() {
        MenuGroup menuGroup = MenuGroupFixture.create();
        MenuGroup actualResult = menuGroupService.create(menuGroup);

        assertEquals(menuGroup.getName(), actualResult.getName());
    }

    @DisplayName("[예외] 상품의 이름은 null 이거나 empty 일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create_fail_because_null_or_empty_name(String name) {
        MenuGroup menuGroup = MenuGroupFixture.create(UUID.randomUUID(), name);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[정상] 메뉴 그룹이 정상적으로 전체 조회됩니다.")
    @Test
    void findAll_success() {
        MenuGroup chicken = menuGroupRepository.save(MenuGroupFixture.create(UUID.randomUUID(), "치킨"));
        MenuGroup pizza = menuGroupRepository.save(MenuGroupFixture.create(UUID.randomUUID(), "피자"));

        List<MenuGroup> actualResult = menuGroupService.findAll();

        assertThat(actualResult).containsExactly(chicken, pizza);
    }


}