package kitchenpos.domain;

import kitchenpos.DummyData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupRepositoryTest extends DummyData {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴그룹 저장")
    @Test
    void save() {
        MenuGroup menuGroup = menuGroups.get(0);

        given(menuGroupRepository.save(menuGroup)).willReturn(menuGroup);

        MenuGroup saveMenuGroup = menuGroupRepository.save(menuGroup);

        assertThat(menuGroup.getId()).isEqualTo(saveMenuGroup.getId());
    }

    @DisplayName("메뉴그룹 내역 조회")
    @Test
    void findAll() {
        when(menuGroupRepository.findAll()).thenReturn(menuGroups);

        List<MenuGroup> findAll = menuGroupRepository.findAll();

        verify(menuGroupRepository).findAll();
        verify(menuGroupRepository, times(1)).findAll();
        assertThat(menuGroups.containsAll(findAll)).isTrue();
    }
}