package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuFixture {
    private static final long quantity = 1;
    private static long sequence = 1;

    private MenuFixture() {}

    public static MenuProduct createMenuProduct(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        menuProduct.setSeq(sequence++);
        return menuProduct;
    }

    public static Menu createMenu(final String name, final MenuGroup menuGroup, final List<Product> products) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        final BigDecimal price = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(BigDecimal.valueOf(100));
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        final List<MenuProduct> menuProducts = products.stream()
                .map(product -> createMenuProduct(product, quantity))
                .collect(Collectors.toList());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private static List<Menu> createMenus(final List<MenuGroup> menuGroups, final List<Product> products) {
        return Arrays.asList(
                createMenu("첫번째 메뉴", menuGroups.get(0), products),
                createMenu("두번째 메뉴", menuGroups.get(1), products.subList(0, 2)),
                createMenu("세번째 메뉴", menuGroups.get(2), products.subList(1, 3))
        );
    }

    public static MenuRepository createMenuRepository(final MenuGroupRepository menuGroupRepository, final ProductRepository productRepository) {
        final MenuRepository menuRepository = new FakeMenuRepository();
        createMenus(menuGroupRepository.findAll(), productRepository.findAll())
                .forEach(menuRepository::save);
        return menuRepository;
    }
}
