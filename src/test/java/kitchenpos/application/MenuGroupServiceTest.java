package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @DisplayName("메뉴 그룹 등록")
  @Test
  void createMenuGroup() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setName("추천메뉴");

    when(menuGroupRepository.save(any())).thenReturn(menuGroup);

    MenuGroup result = menuGroupService.create(menuGroup);

    assertThat(result.getName()).isEqualTo("추천메뉴");
  }

  @DisplayName("메뉴 그룹 이름 null 등록 에러")
  @Test
  void createMenuGroupNameNull() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setName(null);

    assertThatThrownBy(() -> menuGroupService.create(menuGroup)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 그룹 이름 빈값 등록 에러")
  @Test
  void createMenuGroupNameEnpty() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setName("");

    assertThatThrownBy(() -> menuGroupService.create(menuGroup)).isInstanceOf(IllegalArgumentException.class);
  }
}