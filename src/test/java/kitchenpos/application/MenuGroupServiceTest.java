package kitchenpos.application;

import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹을 등록한다.")
    @Nested
    class CreateTest {

        @DisplayName("메뉴그룹이 등록된다.")
        @Test
        void createdMenuGroup() {
            // given
            final MenuGroup request = MenuGroupFixture.createRequest("추천메뉴");
            given(menuGroupRepository.save(any())).will(AdditionalAnswers.returnsFirstArg());

            // when
            MenuGroup result = menuGroupService.create(request);

            // then
            assertAll(() -> {
                assertThat(result.getId()).isNotNull();
                assertThat(result.getName()).isEqualTo("추천메뉴");
            });
        }

        @DisplayName("이름은 비어있을 수 없다.")
        @Test
        void null_name() {
            // given
            final MenuGroup request = MenuGroupFixture.createRequest(null);

            // then
            assertThatThrownBy(() -> menuGroupService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
