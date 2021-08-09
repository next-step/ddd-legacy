package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupRepositoryTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private static final UUID MENU_GROUP_ID = UUID.randomUUID();

    @DisplayName("메뉴그룹 저장")
    @Test
    void save() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("추천메뉴");

        when(menuGroupRepository.save(menuGroup)).thenReturn(menuGroup);

        MenuGroup saveMenuGroup = menuGroupRepository.save(menuGroup);

        assertThat(menuGroup.getId()).isEqualTo(saveMenuGroup.getId());
    }

    @DisplayName("메뉴그룹 내역 조회")
    @Test
    void findAll() {
        List<MenuGroup> menuGroupList = new ArrayList<>();

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("양식");

        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("일식");

        menuGroupList.add(menuGroup);
        menuGroupList.add(menuGroup2);

        when(menuGroupRepository.findAll()).thenReturn(menuGroupList);

        List<MenuGroup> findAll = menuGroupRepository.findAll();

        verify(menuGroupRepository).findAll();
        verify(menuGroupRepository, times(1)).findAll();
        assertThat(menuGroupList.containsAll(findAll)).isTrue();
    }
}