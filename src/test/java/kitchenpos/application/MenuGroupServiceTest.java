package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
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

  private MenuGroup menuGroup;

  @BeforeEach
  void setUp() {
    menuGroup = MenuGroupFixture.createMenuGroup();
  }

  @DisplayName("메뉴 그룹 등록")
  @Test
  void createMenuGroup() {
    when(menuGroupRepository.save(any())).thenReturn(menuGroup);

    MenuGroup result = menuGroupService.create(menuGroup);

    assertThat(result.getName()).isEqualTo("추천메뉴");
  }

  @DisplayName("메뉴 그룹 이름 null 등록 에러")
  @Test
  void createMenuGroupNameNull() {
    menuGroup.setName(null);

    assertThatThrownBy(() -> menuGroupService.create(menuGroup)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 그룹 이름 빈값 등록 에러")
  @Test
  void createMenuGroupNameEnpty() {
    menuGroup.setName("");

    assertThatThrownBy(() -> menuGroupService.create(menuGroup)).isInstanceOf(IllegalArgumentException.class);
  }
}