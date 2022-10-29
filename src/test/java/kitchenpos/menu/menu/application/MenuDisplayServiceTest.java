package kitchenpos.menu.menu.application;

import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("메뉴 전시 서비스")
class MenuDisplayServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuDisplayService menuDisplayService;

    private Product 상품;
    private Menu 메뉴;

    @BeforeEach
    void setUp() {
        상품 = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(new MenuProduct(상품, new Quantity(1)));
        MenuGroup 메뉴그룹 = menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴 그룹명", false)));
        메뉴 = menuRepository.save(new Menu(UUID.randomUUID(), new Name("메뉴명", false), 메뉴그룹, menuProducts, new Price(BigDecimal.TEN)));
    }

    @DisplayName("메뉴를 보일 수 있다.")
    @Test
    void display() {
        assertThat(메뉴.isDisplayed()).isTrue();
        menuDisplayService.hide(메뉴.getId());
        assertThat(메뉴.isDisplayed()).isFalse();
        menuDisplayService.display(메뉴.getId());
        assertThat(메뉴.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        assertThat(메뉴.isDisplayed()).isTrue();
        menuDisplayService.hide(메뉴.getId());
        assertThat(메뉴.isDisplayed()).isFalse();
    }
}
