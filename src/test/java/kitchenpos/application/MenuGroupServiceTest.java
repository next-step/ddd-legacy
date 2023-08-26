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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService sut;

    private static final UUID uuid = UUID.randomUUID();

    @ParameterizedTest(name = "메뉴의 그룹의 이름이 없으면 메뉴 그룹을 생성할 수 없다: name = {0}")
    @NullAndEmptySource
    void notCreateMenuGroupWithoutName(String name) {
        // given
        MenuGroup menuGroup = createMenuGroup(name);

        // when & then
        assertThatThrownBy(() -> sut.create(menuGroup))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹을 생성할 수 있다")
    @Test
    void createMenuGroup() {
        // given
        MenuGroup request = createMenuGroup("메뉴 그룹");

        given(menuGroupRepository.save(any())).willReturn(request);

        // when
        MenuGroup result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(MenuGroup.class);
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(uuid);
        menuGroup.setName(name);
        return menuGroup;
    }
}
