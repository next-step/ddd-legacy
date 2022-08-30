package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import kitchenpos.constant.Fixtures;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        Fixtures.initialize();
    }

    @DisplayName("메뉴 그룹 등록")
    @Nested
    public class CreateTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @DisplayName("정상 동작")
            @Test
            void create() {
                // given
                given(menuGroupRepository.save(any())).willReturn(any());

                // when
                menuGroupService.create(Fixtures.MENU_GROUP);

                // then
                then(menuGroupRepository).should().save(any());
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("이름이 null 또는 빈값일 수 없음")
            @ParameterizedTest
            @NullAndEmptySource
            void createWithNullOrEmptyName(String name) {
                // given
                MenuGroup menuGroup = new MenuGroup();
                menuGroup.setName(name);

                // when then
                assertThatIllegalArgumentException().isThrownBy(
                    () -> menuGroupService.create(menuGroup)
                );
            }
        }
    }

    @DisplayName("모든 메뉴 그룹 조회")
    @Test
    void findAll() {
        // given
        given(menuGroupRepository.findAll()).willReturn(List.of(Fixtures.MENU_GROUP));

        // when
        List<MenuGroup> results = menuGroupService.findAll();

        // then
        assertThat(results).containsExactly(Fixtures.MENU_GROUP);
    }
}
