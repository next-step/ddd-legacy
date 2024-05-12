package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Nested
    @DisplayName("메뉴 그룹 생성")
    class MenuGroupCreation {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("메뉴 그룹 이름이 null이거나 비어있으면 예외가 발생한다.")
        void shouldThrowExceptionWhenCreatingWithNullOrEmptyName(String name) {
            // given
            MenuGroup request = MenuFixture.메뉴_그룹_생성(name);

            // when & then
            Assertions.assertThatThrownBy(() -> menuGroupService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 그룹을 생성할 수 있다.")
        void shouldCreateMenuGroupSuccessfully() {
            // given
            MenuGroup request = MenuFixture.기본_메뉴_그룹();

            // when
            when(menuGroupRepository.save(any())).thenReturn(request);
            menuGroupService.create(request);

            // then
            verify(menuGroupRepository, times(1)).save(any());
        }
    }

}