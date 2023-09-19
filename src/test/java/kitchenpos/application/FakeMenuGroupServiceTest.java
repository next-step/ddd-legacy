package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.fake.FakeMenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FakeMenuGroupServiceTest {

    private MenuGroupService menuGroupService;

    public FakeMenuGroupServiceTest() {
        this.menuGroupService = new MenuGroupService(new FakeMenuGroupRepository());
    }

    @DisplayName("메뉴그룹을 등록한다.")
    @Test
    void createMenuGroupTest() {
        final MenuGroup menuGroup = MenuGroupFixture.create();
        final MenuGroup createMenuGroup = menuGroupService.create(menuGroup);

        assertThat(menuGroup.getName()).isEqualTo(createMenuGroup.getName());
    }

    @DisplayName("메뉴 그룹은 비어 있으면 에러를 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menuGroupNameException(String name) {
        final MenuGroup menuGroup = MenuGroupFixture.create(name);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
