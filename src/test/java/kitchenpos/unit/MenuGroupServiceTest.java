package kitchenpos.unit;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.unit.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.unit.fixture.MenuGroupFixture.탕수육_세트;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 등록한다")
    @Test
    void create() {
        // given
        when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(탕수육_세트);

        // when
        MenuGroup saveMenuGroup = menuGroupService.create(탕수육_세트);

        // then
        assertThat(saveMenuGroup.getName()).isEqualTo(탕수육_세트.getName());
    }

    @DisplayName("메뉴 그룹의 이름이 null인 경우 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    void createInvalidName(String name) {
        assertThatThrownBy(() -> menuGroupService.create(createMenuGroup(name)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}