package kitchenpos.domain;

import kitchenpos.FixtureData;
import org.junit.jupiter.api.BeforeEach;
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
public class MenuRepositoryTest extends FixtureData {

    @Mock
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        fixtureMenus();
    }

    @DisplayName("메뉴 생성")
    @Test
    void createMenu() {
        // given
        Menu menu = menus.get(0);

        given(menuRepository.save(menu)).willReturn(menu);

        // when
        Menu createMenu = menuRepository.save(menu);

        // then
        assertThat(createMenu).isEqualTo(menu);
    }

    @DisplayName("메뉴 노출 변경")
    @Test
    void changeDisplay() {
        // given
        given(menuRepository.findById(FIRST_ID)).willReturn(Optional.of(menus.get(0)));

        Menu findMenu = menuRepository.findById(FIRST_ID).get();
        findMenu.setDisplayed(MENU_HIDE);

        given(menuRepository.save(findMenu)).willReturn(findMenu);

        // when
        Menu changeMenu = menuRepository.save(findMenu);

        // then
        assertThat(changeMenu.isDisplayed()).isEqualTo(MENU_HIDE);
    }

    @DisplayName("메뉴 내역 조회")
    @Test
    void findAll() {
        // given
        given(menuRepository.findAll()).willReturn(menus);

        // then
        List<Menu> findAll = menuRepository.findAll();

        // when
        verify(menuRepository).findAll();
        verify(menuRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(menus.containsAll(findAll)).isTrue(),
                () -> assertThat(menus.size()).isEqualTo(findAll.size())
        );
    }
}
