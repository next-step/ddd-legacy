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

import static kitchenpos.fixture.MenuGroupFixture.MenuGroupBuilder;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @InjectMocks
    MenuGroupService menuGroupService;
    @Mock
    MenuGroupRepository menuGroupRepository;

    private static Stream<String> 잘못된_메뉴그룹명() {
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName(value = "메뉴그룹을 등록할 수 있다")
    @Test
    void create_success() throws Exception {
        //given
        MenuGroup 등록할메뉴그룹 = new MenuGroupBuilder().name("한마리메뉴").build();

        //when
        menuGroupService.create(등록할메뉴그룹);

        //then
        verify(menuGroupRepository, times(1)).save(any(MenuGroup.class));
    }

    @DisplayName(value = "반드시 한글자 이상의 메뉴그룹명을 가진다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴그룹명")
    void create_fail_invalid_name(final String 메뉴그룹명) throws Exception {
        //given
        MenuGroup 등록할메뉴그룹 = new MenuGroupBuilder().name(메뉴그룹명).build();;

        //when, then
        assertThatThrownBy(() -> menuGroupService.create(등록할메뉴그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "전체 메뉴그룹을 조회할 수 있다")
    @Test
    void findAll_success() throws Exception {
        //given, when
        menuGroupService.findAll();

        //then
        verify(menuGroupRepository, times(1)).findAll();
    }
}
