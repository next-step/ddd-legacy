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
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

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

    @Test
    void 메뉴_생성_실패__메뉴상품의_갯수가_음수() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(new Product()));
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(-1);
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(0));
        menu.setMenuProducts(List.of(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__구성메뉴상품의_가격_총합이_메뉴_가격_보다_이상일_수_없다() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        Product product = new Product();
        product.setPrice(new BigDecimal(500));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(1001));
        menu.setMenuProducts(List.of(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__이름이_null() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        Product product = new Product();
        product.setPrice(new BigDecimal(500));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(1000));
        menu.setMenuProducts(List.of(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_생성_실패__이름에_욕설_포함() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        Product product = new Product();
        product.setPrice(new BigDecimal(500));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(1000));
        menu.setMenuProducts(List.of(menuProduct));
        menu.setName("abuse name");
        when(purgomalumClient.containsProfanity("abuse name")).thenReturn(true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
