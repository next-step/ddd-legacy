package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.menu.TestMenuRepository;
import kitchenpos.fake.menuGroup.TestMenuGroupRepository;
import kitchenpos.fake.product.TestProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.TestFixture.createMenuProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MenuServiceTest {
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        menuRepository = new TestMenuRepository();
        menuGroupRepository = new TestMenuGroupRepository();
        productRepository = new TestProductRepository();
    }

    @Test
    void createMenu() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        menuRequest.setMenuProducts(List.of(menuProduct));
        menuRequest.setDisplayed(true);

        // when
        Menu createdMenu = menuService.create(menuRequest);

        // then
        assertAll(
                () -> assertThat(createdMenu.getId()).isNotNull(),
                () -> assertThat(createdMenu.getPrice()).isEqualTo(menuPrice),
                () -> assertTrue(createdMenu.isDisplayed()),
                () -> assertThat(createdMenu.getName()).isEqualTo(menuName),
                () -> assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId()),
                () -> assertThat(createdMenu.getMenuProducts().size()).isEqualTo(1),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getQuantity()).isEqualTo(2),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getId()).isEqualTo(product.getId())
        );

    }
}
