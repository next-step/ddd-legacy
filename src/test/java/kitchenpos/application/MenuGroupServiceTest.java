package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private static Stream<String> provideMenuNameForNullAndEmptyString() { // argument source method
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName("메뉴 그룹 등록 - 메뉴 그룹은 반드시 이름을 가져야 한다.")
    @MethodSource("provideMenuNameForNullAndEmptyString")
    @ParameterizedTest
    public void create01(String name) {
        //given
        MenuGroup 등록할_메뉴_그룹 = mock(MenuGroup.class);
        when(등록할_메뉴_그룹.getName()).thenReturn(name);

        //when & then
        assertThatThrownBy(() -> menuGroupService.create(등록할_메뉴_그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 등록 - 메뉴그룹을 등록 할 수 있다.")
    @Test
    public void create02() {
        //given
        MenuGroup 등록할_메뉴_그룹 = mock(MenuGroup.class);
        String 등록할_메뉴_그룹_이름 = "런치 세트 메뉴";
        when(등록할_메뉴_그룹.getName()).thenReturn(등록할_메뉴_그룹_이름);
        //when
        menuGroupService.create(등록할_메뉴_그룹);
        //then
        verify(menuGroupRepository).save(any(MenuGroup.class));
    }

    @DisplayName("메뉴 그룹 조회 - 등록된 모든 메뉴 그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        // given & when
        menuGroupService.findAll();
        //then
        verify(menuGroupRepository).findAll();
    }

}