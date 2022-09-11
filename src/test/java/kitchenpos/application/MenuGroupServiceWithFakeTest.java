package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.fake.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuGroupServiceWithFakeTest {

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴그룹 등록")
    @Nested
    class CreateTest {

        @DisplayName("등록 성공")
        @Test
        void createdMenuGroup() {
            // given
            final MenuGroup request = MenuGroupFixture.createRequest("추천메뉴");

            // when
            MenuGroup result = menuGroupService.create(request);

            // then
            assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("추천메뉴")
            );
        }

        @ParameterizedTest(name = "이름은 비어있을 수 없다. name={0}")
        @NullAndEmptySource
        void null_and_empty_name(String name) {
            // given
            final MenuGroup request = MenuGroupFixture.createRequest(name);

            // then
            assertThatThrownBy(() -> menuGroupService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
