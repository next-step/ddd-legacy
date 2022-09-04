package kitchenpos.application;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService testService;

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

            given(menuGroupRepository.save(any())).willAnswer((invocation) -> invocation.getArgument(0));

            // when
            final var result = testService.create(request);

            // then
            assertThat(result.getName()).isEqualTo("초밥");
        }
    }

    @DisplayName("모든 메뉴그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final var menuGroup1 = new MenuGroup();
        menuGroup1.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        final var menuGroup2 = new MenuGroup();
        menuGroup2.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        final var menuGroupsInRepo = List.of(menuGroup1, menuGroup2);
        given(menuGroupRepository.findAll()).willReturn(menuGroupsInRepo);

        // when
        final var result = testService.findAll();

        // then
        assertThat(result).hasSize(2)
                .extracting(MenuGroup::getId)
                .containsExactlyInAnyOrder(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        UUID.fromString("22222222-2222-2222-2222-222222222222")
                );
    }
}
