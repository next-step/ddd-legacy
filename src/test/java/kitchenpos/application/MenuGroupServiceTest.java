package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.infra.InMemoryMenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴그룹 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

  private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

  private MenuGroupService menuGroupService;

  @BeforeEach
  void setUp() {
    this.menuGroupService = new MenuGroupService(menuGroupRepository);
  }

  @Nested
  @DisplayName("메뉴그룹을 등록할 때")
  class WhenCreate {

    @DisplayName("유효한 메뉴그룹명을 입력하면 메뉴그룹을 등록할 수 있다.")
    @Test
    void givenValidMenuGroup_whenCreate_thenMenuGroup() {
      // given
      MenuGroup menuGroup = creationRequestMenuGroup("추천메뉴");

      // when
      MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

      // then
      Assertions.assertThat(createdMenuGroup.getName()).isEqualTo("추천메뉴");
    }

    @DisplayName("메뉴그룹 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
    void givenNotValidPrice_whenCreate_thenIllegalArgumentException(String name) {
      // given
      MenuGroup menuGroup = creationRequestMenuGroup(name);

      // when & then
      assertThatIllegalArgumentException().isThrownBy(() -> menuGroupService.create(menuGroup));
    }
  }

  @Nested
  @DisplayName("메뉴그룹을 조회 할 때")
  class WhenFind {

    @DisplayName("등록된 메뉴그룹을 조회하면 등록된 메뉴그룹 목록을 반환한다.")
    @Test
    void givenMenuGroups_whenFindAll_thenReturnMenuGroups() {
      // given
      List.of(creationRequestMenuGroup("추천메뉴"), creationRequestMenuGroup("점심특선메뉴"))
          .forEach(menuGroupRepository::save);

      // when
      List<MenuGroup> menuGroups = menuGroupService.findAll();

      // then
      assertThat(menuGroups).hasSize(2);
      assertThat(menuGroups).extracting(MenuGroup::getName).contains("추천메뉴", "점심특선메뉴");
    }
  }

  private static MenuGroup creationRequestMenuGroup(String name) {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName(name);
    return menuGroup;
  }

}
