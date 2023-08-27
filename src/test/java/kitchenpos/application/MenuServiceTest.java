package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

class MenuServiceTest {


    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    @DisplayName("메뉴 생성시 가격이 null 이면 예외를 발생시킨다.")
    @Test
    void create_price_null() {
        Menu menu = new Menu();
        menu.setPrice(null);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 생성시 가격이 음수면 예외를 발생시킨다.")
    @Test
    void create_price_negative() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(-1));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }


}
