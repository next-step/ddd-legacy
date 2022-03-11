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

import java.util.ArrayList;
import java.util.List;

import static kitchenpos.KitchenposFixture.*;
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
    MenuGroup menuGroup = menuGroup();
    menuGroup.setName(name);

    //then
    assertThatThrownBy(() -> menuGroupService.create(menuGroup))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("메뉴 그룹 생성")
  void createMenuGroup() {
    //given
    MenuGroup menuGroup = menuGroup();

    when(menuGroupRepository.save(any())).thenReturn(menuGroup);

    assertDoesNotThrow(()->{
      MenuGroup createMenuGroup = menuGroupService.create(menuGroup);
    });
  }

  @Test
  @DisplayName("메뉴 그룹 전체 조회")
  void findMenuGroupAll() {
    //given
    List<MenuGroup> menuGroupList = new ArrayList<>();
    menuGroupList.add(menuGroup());
    menuGroupList.add(menuGroup());

    when(menuGroupRepository.findAll()).thenReturn(menuGroupList);

    List<MenuGroup> findMenuGroupList = menuGroupService.findAll();

    verify(menuGroupRepository).findAll();

    assertThat(findMenuGroupList.size()).isEqualTo(menuGroupList.size());
    assertThat(menuGroupList).isSameAs(findMenuGroupList);
  }

}
