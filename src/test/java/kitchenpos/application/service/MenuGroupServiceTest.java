package kitchenpos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        menuGroup = MenuGroupFixture.init().create();
    }

    @Nested
    @DisplayName("메뉴 그룹 조회")
    class 메뉴_그룹_조회 {

        @Test
        @DisplayName("성공 : 특정 조건 없이 상품의 모든 목록을 조회할 수 있다.")
        void 메뉴그룹목록_조회() {
            when(menuGroupRepository.findAll()).thenReturn(List.of(menuGroup));
            List<MenuGroup> result = menuGroupService.findAll();

            assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertEquals(result.size(), 1)
            );
        }
    }

    @Nested
    @DisplayName("메뉴 그룹 등록")
    class 메뉴그룹_등록 {

        @Test
        @DisplayName("성공")
        void 메뉴그룹_등록_성공() {
            when(menuGroupRepository.save(Mockito.any(MenuGroup.class))).thenReturn(menuGroup);

            var result = menuGroupService.create(menuGroup);

            assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getName(), menuGroup.getName())
            );

        }

        @ParameterizedTest
        @DisplayName("메뉴 그룹명을 반드시 가진다.")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        void 메뉴그룹명_유효성_검사(final String name) {
            menuGroup = MenuGroupFixture.test(name).create();

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(menuGroup));
        }
    }

}
