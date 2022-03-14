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

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.정상_메뉴_그룹;
import static kitchenpos.fixture.MenuGroupFixture.정상_메뉴_그룹_리스트;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

  @Mock
  private MenuGroupRepository menuGroupRepository;

  @InjectMocks
  private MenuGroupService menuGroupService;

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("이름이 null이거나 empty이면 IllegalArgumentException 예외 발생")
  void createMenuGroupButNameless(String name) {
    //given
    MenuGroup menuGroup = 정상_메뉴_그룹();
    menuGroup.setName(name);

    //then
    assertThatThrownBy(() -> menuGroupService.create(menuGroup))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("메뉴 그룹 생성")
  void createMenuGroup() {
    //given
    MenuGroup request = 정상_메뉴_그룹();

    when(menuGroupRepository.save(any())).thenReturn(request);

    assertDoesNotThrow(() -> {
      MenuGroup result = menuGroupService.create(request);
      assertThat(request.getName()).isEqualTo(result.getName());
    });
  }

  @Test
  @DisplayName("메뉴 그룹 전체 조회")
  void findMenuGroupAll() {
    //given
    List<MenuGroup> menuGroupList = 정상_메뉴_그룹_리스트();

    when(menuGroupRepository.findAll()).thenReturn(menuGroupList);

    List<MenuGroup> result = menuGroupService.findAll();

    verify(menuGroupRepository).findAll();

    assertThat(result.size()).isEqualTo(menuGroupList.size());
    assertThat(menuGroupList).isSameAs(result);
  }
}
