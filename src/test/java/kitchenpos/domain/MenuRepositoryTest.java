package kitchenpos.domain;

import kitchenpos.DummyData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MenuRepositoryTest extends DummyData {

    @Mock
    private MenuRepository menuRepository;

    @DisplayName("메뉴 생성")
    @Test
    void createMenu() {
        Menu menu = menus.get(0);

        given(menuRepository.save(menu)).willReturn(menu);

        Menu createMenu = menuRepository.save(menu);

        assertThat(createMenu).isEqualTo(menu);
    }

    @DisplayName("메뉴 노출 변경")
    @Test
    void changeDisplay() {
        given(menuRepository.findById(FIRST_ID)).willReturn(Optional.of(menus.get(0)));

        Menu findMenu = menuRepository.findById(FIRST_ID).get();
        findMenu.setDisplayed(MENU_HIDE);

        given(menuRepository.save(findMenu)).willReturn(findMenu);

        Menu changeMenu = menuRepository.save(findMenu);

        assertThat(changeMenu.isDisplayed()).isEqualTo(MENU_HIDE);
    }

    @DisplayName("메뉴 내역 조회")
    @Test
    void findAll() {
        given(menuRepository.findAll()).willReturn(menus);

        List<Menu> findAll = menuRepository.findAll();

        verify(menuRepository).findAll();
        verify(menuRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(menus.containsAll(findAll)).isTrue(),
                () -> assertThat(menus.size()).isEqualTo(findAll.size())
        );
    }
}
