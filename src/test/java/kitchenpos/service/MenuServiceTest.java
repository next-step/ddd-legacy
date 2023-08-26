package kitchenpos.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    void 메뉴_생성_실패__가격이_null() {
        Menu menu = new Menu();
        menu.setPrice(null);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__가격이_음수() {
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴상품이_null() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(0));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴상품이_0개() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(0));
        menu.setMenuProducts(List.of());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__메뉴_생성_요청의_메뉴상품이_존재하지_않음() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(0));
        menu.setMenuProducts(List.of(new MenuProduct()));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
