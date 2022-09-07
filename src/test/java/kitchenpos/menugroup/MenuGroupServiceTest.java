package kitchenpos.menugroup;

import kitchenpos.application.MenuGroupService;
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

import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.menugroup.MenuGroupFixture.menuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        MenuGroup 추천메뉴 = menuGroup("추천메뉴");
        when(menuGroupRepository.save(any())).thenReturn(추천메뉴);

        MenuGroup 추천메뉴_그룹 = menuGroupService.create(추천메뉴);

        assertThat(추천메뉴_그룹.getName()).isEqualTo("추천메뉴");
    }

    @DisplayName("메뉴 그룹을 생성할 때 이름이 있어야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithNullName(String name) {
        assertThatThrownBy(() -> menuGroupService.create(menuGroup(name)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹 목록을 조회한다.")
    @Test
    void findAll() {
        MenuGroup 추천메뉴 = menuGroup("추천메뉴");
        MenuGroup 이달의메뉴 = menuGroup("이달의메뉴");
        MenuGroup 계절메뉴 = menuGroup("계절메뉴");
        MenuGroup 올해의메뉴 = menuGroup("올해의메뉴");
        when(menuGroupRepository.findAll()).thenReturn(List.of(추천메뉴, 이달의메뉴, 계절메뉴, 올해의메뉴));

        List<String> 메뉴그룹_목록 = menuGroupService.findAll().stream()
                .map(MenuGroup::getName)
                .collect(Collectors.toList());

        assertThat(메뉴그룹_목록).containsExactly("추천메뉴", "이달의메뉴", "계절메뉴", "올해의메뉴");
    }
}
