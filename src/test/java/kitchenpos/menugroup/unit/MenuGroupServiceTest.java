package kitchenpos.menugroup.unit;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.menugroup.fixture.MenuGroupFixture.A_메뉴그룹;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.빈문자이름_메뉴그룹;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.이름미존재_메뉴그룹;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        this.menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    class 이름검증 {

        @Test
        @DisplayName("[성공] 메뉴 그룹의 이름을 A로 입력하면 생성된다.")
        void 메뉴그룹_이름_A() {
            // given
            given(menuGroupRepository.save(any())).willReturn(A_메뉴그룹);

            // when
            var saved = menuGroupService.create(A_메뉴그룹);

            // then
            assertAll(
                    () -> then(menuGroupRepository).should(times(1)).save(any()),
                    () -> assertThat(saved.getName()).isEqualTo(A_메뉴그룹.getName())
            );
        }

        @Test
        @DisplayName("[실패] 메뉴 그룹의 이름을 입력하지 않으면 생성이 되지 않는다.")
        void 메뉴그룹_이름_null() {
            assertThatThrownBy(() -> menuGroupService.create(이름미존재_메뉴그룹))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[실패] 메뉴 그룹의 이름이 빈문자열이면 생성이 되지 않는다.")
        void 메뉴그룹_이름_빈문자열() {
            assertThatThrownBy(() -> menuGroupService.create(빈문자이름_메뉴그룹))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }
}
