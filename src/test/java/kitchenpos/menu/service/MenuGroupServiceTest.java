package kitchenpos.menu.service;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.menu.fixture.MenuGroupFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 그룹 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @InjectMocks
    private MenuGroupService menuGroupService;

    private MenuGroupFixture menuGroupFixture;

    @BeforeEach
    void setUp() {
        menuGroupFixture = new MenuGroupFixture();
    }

    @Test
    @DisplayName("새로운 메뉴 그룹을 추가할 수 있다.")
    void create() {
        MenuGroup 한식 = menuGroupFixture.메뉴_그룹_A;

        Mockito.when(menuGroupRepository.save(Mockito.any()))
                .thenReturn(한식);

        MenuGroup result = menuGroupService.create(한식);

        Assertions.assertThat(result.getName()).isEqualTo(한식.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 그룹 추가 시 이름이 반드시 존재해야 한다.")
    void test(String name) {
        Assertions.assertThatThrownBy(
                () -> menuGroupService.create(MenuGroupFixture.create(name))
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
