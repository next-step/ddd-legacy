package kitchenpos.application;

import static kitchenpos.application.MenuGroupFixture.메뉴판;
import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuGroupFixture.추천메뉴;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 그룹")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("이름이 없으면 예외 발생")
    @ParameterizedTest(name = "이름: [{arguments}]")
    @NullAndEmptySource
    void createNonameException(String name) {
        //given
        MenuGroup 이름_없는_메뉴_그룹 = new MenuGroup();
        이름_없는_메뉴_그룹.setName(name);

        //when
        ThrowingCallable actual = () -> menuGroupService.create(이름_없는_메뉴_그룹);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 생성")
    @Test
    void create() {
        //given
        given(menuGroupRepository.save(any(MenuGroup.class))).willReturn(세트메뉴);

        //when
        MenuGroup menuGroup = menuGroupService.create(세트메뉴);

        //then
        assertAll(
            () -> assertThat(menuGroup.getId()).isNotNull(),
            () -> assertThat(menuGroup.getName()).isEqualTo(세트메뉴.getName())
        );
    }

    @DisplayName("모든 메뉴 그룹 조회")
    @Test
    void findAll() {
        //given
        given(menuGroupRepository.findAll()).willReturn(메뉴판);

        //when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertAll(
            () -> assertThat(menuGroups).hasSize(2),
            () -> assertThat(menuGroups).extracting("name")
                .containsExactly(세트메뉴.getName(), 추천메뉴.getName())
        );
    }
}
