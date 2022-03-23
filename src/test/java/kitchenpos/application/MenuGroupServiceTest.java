package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService sut;

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴그룹 생성 시 빈 이름은 사용할 수 없다.")
    void noEmptyNameTest(String name) {
        // given
        MenuGroup request = new MenuGroup();
        request.setName(name);

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴그룹 생성이 성공하면 임의 생성된 ID와 request로 입력한 name 값을 가진다.")
    void successCreateMenuGroup() {
        // given
        MenuGroup request = new MenuGroup();
        request.setName("SUCCESS-REQUEST");

        // when
        sut.create(request);

        // then
        then(menuGroupRepository).should()
                                 .save(argThat(menuGruop -> validateArgs(request, menuGruop)));
    }

    private boolean validateArgs(MenuGroup request, MenuGroup menuGroup) {
        return menuGroup != null
            && StringUtils.isNotBlank(menuGroup.getId().toString())
            && request.getName().equals(menuGroup.getName());
    }
}
