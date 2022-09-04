package kitchenpos.application;

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
import static org.mockito.Mockito.when;

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

            when(menuGroupRepository.save(any())).thenAnswer((invocation) -> invocation.getArgument(0));

            // when
            final var result = testService.create(request);

            // then
            assertThat(result.getName()).isEqualTo("초밥");
        }
    }
}
