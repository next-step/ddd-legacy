package kitchenpos.application;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuGroupServiceTest {
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService testService;

    @BeforeEach
    void setUp() {
        this.testService = new MenuGroupService(menuGroupRepository);

        final var menuGroup1 = new MenuGroup();
        menuGroup1.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        final var menuGroup2 = new MenuGroup();
        menuGroup2.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        final var menuGroupsInRepo = List.of(menuGroup1, menuGroup2);

        menuGroupRepository.saveAll(menuGroupsInRepo);
    }

    @DisplayName("메뉴그룹 생성")
    @Nested
    class Create {
        @DisplayName("이름은 비어 있지 않아야 한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void nullOrEmptyName(String name) {
            // given
            final var request = new MenuGroup();
            request.setName(name);

            // when
            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴그룹을 등록할 수 있다.")
        @Test
        void create() {
            // given
            final var request = new MenuGroup();
            request.setName("초밥");

            // when
            final var result = testService.create(request);

            // then
            assertThat(result.getName()).isEqualTo("초밥");
        }
    }

    @DisplayName("모든 메뉴그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        final var result = testService.findAll();

        assertThat(result).hasSize(2)
                .extracting(MenuGroup::getId)
                .containsExactlyInAnyOrder(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        UUID.fromString("22222222-2222-2222-2222-222222222222")
                );
    }
}
