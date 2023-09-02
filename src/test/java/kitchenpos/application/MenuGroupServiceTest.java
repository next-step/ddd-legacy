package kitchenpos.application;

import kitchenpos.ApplicationServiceTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MenuGroupServiceTest extends ApplicationServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("[정상] 메뉴 그룹이 정상적으로 등록됩니다.")
    @Test
    void create_success() {
        MenuGroup menuGroup = MenuGroupFixture.create();
        menuGroupService.create(menuGroup);
    }

    @DisplayName("[예외] 상품의 이름은 null 이거나 empty 일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create_fail_because_null_or_empty_name(String name) {
        MenuGroup menuGroup = MenuGroupFixture.create(UUID.randomUUID(), name);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

}