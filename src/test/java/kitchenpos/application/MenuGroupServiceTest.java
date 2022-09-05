package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

  private MenuGroupService menuGroupService;

  @Mock
  private MenuGroupRepository menuGroupRepository;

  @BeforeEach
  void setUp() {
    this.menuGroupService = new MenuGroupService(menuGroupRepository);
  }

  @DisplayName("유효한 메뉴그룹명을 입력하면 메뉴그룹을 등록할 수 있다.")
  @Test
  void givenValidMenuGroup_whenCreate_thenMenuGroup() {
    // given
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName("추천메뉴");

    given(menuGroupRepository.save(any(MenuGroup.class))).willReturn(menuGroup);

    // when
    MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

    // then
    Assertions.assertThat(createdMenuGroup.getName()).isEqualTo(menuGroup.getName());
  }

  @DisplayName("메뉴그룹 이름은 비어있을 수 없다.")
  @NullAndEmptySource
  @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
  void givenNotValidPrice_whenCreate_thenIllegalArgumentException(String name) {
    // given
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName(name);

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuGroupService.create(menuGroup));
  }

}
