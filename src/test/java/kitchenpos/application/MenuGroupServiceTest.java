package kitchenpos.application;


import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;
    
    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    @DisplayName("메뉴 그룹을 정상적으로 등록할 수 있다.")
    void success() {
        final var menuGroup = createMenuGroup("메뉴그룹명");
        final var response = createMenuGroup("메뉴그룹명");

        given(menuGroupRepository.save(any())).willReturn(response);

        MenuGroup actual = menuGroupService.create(menuGroup);

        assertAll(
                "메뉴 그룹 Assertions",
                () -> assertNotNull(actual),
                () -> assertEquals(actual.getName(), response.getName())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 그룹의 이름은 필수로 입력해야한다.")
    void name(final String input) {
        MenuGroup menuGroup = createMenuGroup(input);

        assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(menuGroup));
    }

    private static @NotNull MenuGroup createMenuGroup(String name) {
        final var menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
