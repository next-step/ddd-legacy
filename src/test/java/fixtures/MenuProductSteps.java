package fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

public class MenuProductSteps {

    private MenuGroupRepository menuGroupRepository;

    private MenuRepository menuRepository;

    private ProductRepository productRepository;

    public MenuProductSteps(
            MenuGroupRepository menuGroupRepository,
            MenuRepository menuRepository,
            ProductRepository productRepository
    ) {
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
        this.productRepository = productRepository;
    }

    public MenuGroup 메뉴그룹_생성한다() {
        return menuGroupRepository.save(new MenuGroupBuilder().withName("한 마리 메뉴").build());
    }

    public Menu 감추기된_메뉴를_생성한다() {
        final MenuGroup menuGroup = 메뉴그룹_생성한다();
        return 감추기된_메뉴를_생성한다(menuGroup);
    }

    public Menu 감추기된_메뉴를_생성한다(MenuGroup menuGroup) {
        final MenuProduct menuProduct = 메뉴상품을_생성한다();
        return 메뉴를_생성한다("치킨", 10_000, menuGroup, false, List.of(menuProduct));
    }

    public Menu 노출된_메뉴를_생성한다() {
        final MenuGroup menuGroup = 메뉴그룹_생성한다();
        return 노출된_메뉴를_생성한다(menuGroup);
    }

    public Menu 노출된_메뉴를_생성한다(MenuGroup menuGroup) {
        final MenuProduct menuProduct = 메뉴상품을_생성한다();
        return 메뉴를_생성한다("치킨", 10_000, menuGroup, true, List.of(menuProduct));
    }


    public MenuProduct 메뉴상품을_생성한다(Product product) {
        return new MenuProductBuilder()
                .withProduct(product)
                .withQuantity(1)
                .build();
    }

    public MenuProduct 메뉴상품을_생성한다() {
        final Product product = 상품을_생성한다();
        return 메뉴상품을_생성한다(product);
    }

    public Product 상품을_생성한다() {
        return productRepository.save(new ProductBuilder().with("치킨", BigDecimal.valueOf(10_000)).build());
    }

    public Product 상품을_생성한다(Product product){
        return productRepository.save(product);
    }


    public Menu 메뉴를_생성한다(
            String name,
            int price,
            MenuGroup menuGroup,
            boolean displayed,
            List<MenuProduct> menuProducts
    ) {
        return menuRepository.save(
                new MenuBuilder()
                        .withMenuGroup(menuGroup)
                        .withDisplayed(displayed)
                        .with(name, BigDecimal.valueOf(price))
                        .withMenuProducts(menuProducts)
                        .build()
        );
    }

    public Menu 메뉴를_생성한다(
    ) {
        final MenuGroup menuGroup = 메뉴그룹_생성한다();
        return 메뉴를_생성한다("치킨", 10000, menuGroup, true, List.of());
    }
}
