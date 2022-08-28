package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService target;

    private MenuGroup buildValidMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("치킨류");

        return menuGroup;
    }

    @Test
    @DisplayName("메뉴그룹은 이름을 가진다")
    public void create() {
        MenuGroup request = buildValidMenuGroup();

        target.create(request);

        Mockito.verify(menuGroupRepository).save(Mockito.any());
    }

    @ParameterizedTest
    @DisplayName("이름은 빈 값이거나 빈 문자열일 수 없다.")
    @NullAndEmptySource
    public void noEmptyOrNullName(String name) {
        MenuGroup request = buildValidMenuGroup();
        request.setName(name);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(menuGroupRepository);
    }
}