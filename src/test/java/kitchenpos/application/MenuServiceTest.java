package kitchenpos.application;

import fixtures.MenuBuilder;
import fixtures.MenuGroupBuilder;
import fixtures.MenuProductBuilder;
import fixtures.ProductBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;


    @DisplayName("메뉴 생성 테스트")
    @Test
    void createMenuTest() {

        Product product = new ProductBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .build();

        Menu menu = createMenu(productRepository.save(product));

        assertTrue(menu.isDisplayed());
    }


    @DisplayName("메뉴 숨김처리 된다")
    @Test
    void menuHideTest() {

        Product product = new ProductBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .build();

        Menu menu = createMenu(productRepository.save(product));

        Menu hiddenMenu = menuService.hide(menu.getId());
        assertFalse(hiddenMenu.isDisplayed());
    }

    @DisplayName("메뉴 보이기")
    @Test
    void menuDisplayTest() {

        Product product = new ProductBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .build();

        Menu menu = createMenu(productRepository.save(product));

        Menu displayed = menuService.display(menu.getId());
        assertTrue(displayed.isDisplayed());
    }

    private Menu createMenu(Product product) {

        MenuProduct menuProduct = new MenuProductBuilder()
                .withProduct(product)
                .withQuantity(1)
                .build();

        MenuGroup menuGroup = menuGroupRepository.save(new MenuGroupBuilder().withName("한 마리 메뉴").build());
        return menuService.create(new MenuBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .withMenuGroup(menuGroup)
                .withMenuProducts(List.of(menuProduct))
                .build());
    }
}
