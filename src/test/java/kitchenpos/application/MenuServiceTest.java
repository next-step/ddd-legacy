package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class MenuServiceTest {


    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    @DisplayName("메뉴 생성시 가격이 null 이면 예외를 발생시킨다.")
    @Test
    void menu_create_price_null() {
        Menu menu = new Menu();
        menu.setPrice(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 생성시 가격이 음수면 예외를 발생시킨다.")
    @Test
    void menu_create_price_negative() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(-1));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴는 메뉴그룹이 없으면 예외를 발생시킨다.")
    @Test
    void menu_create_not_found_menuGroup() {
        BDDMockito.given(menuGroupRepository.findById(any())).willReturn(Optional.empty());
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(16000));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy( () ->  menuService.create(menu));
    }


}
