package kitchenpos.application.menu;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.menu.FakeMenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class MenuGroupServiceTest {
  public static final String FOR_TWO = "이인용";
  private MenuGroupRepository menuGroupRepository;
  private MenuGroupService menuGroupService;

  @BeforeEach
  void setUp() {
    menuGroupRepository = new FakeMenuGroupRepository();
    menuGroupService = new MenuGroupService(menuGroupRepository);
  }

  @Nested
  @DisplayName("`메뉴 카테고리`(MENU CATEGORY)를 생성할 수 있다.")
  class MenuCateogryRegistration {

    @DisplayName("`메뉴 카테고리`의 `메뉴 카테고리 이름`은 비어있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createMenuCategoryWithEmptyMenuCategoryName(String menuGroupName) {
      MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(menuGroupName);

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("`메뉴 카테고리`의 `메뉴 카테고리 이름`을 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"one for two", "set for two"})
    void createMenuCategory(String menuGroupName) {
      MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(menuGroupName);
      MenuGroup actual = menuGroupService.create(menuGroup);

      assertThat(actual.getName()).isEqualTo(menuGroupName);
    }
  }

  @Nested
  @DisplayName("`메뉴 카테고리`(MENU CATEGORY)를 조회할 수 있다.")
  class MenuCateogryView {
    @Test
    @DisplayName("`메뉴 카테고리`를 조회할 수 있다.")
    void viewMenuCategory() {
      MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(FOR_TWO);
      MenuGroup actual = menuGroupService.create(menuGroup);

      List<MenuGroup> menus = menuGroupService.findAll();

      assertThat(menus).contains(actual);
    }

  }
}
