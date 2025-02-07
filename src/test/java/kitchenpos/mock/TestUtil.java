package kitchenpos.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.mock.fixture.MenuFixture;
import kitchenpos.mock.fixture.MenuGroupFixture;
import kitchenpos.mock.fixture.MenuProductFixture;
import kitchenpos.mock.fixture.OrderTableFixture;
import kitchenpos.mock.fixture.ProductFixture;
import kitchenpos.mock.persistence.FakeMenuGroupRepository;
import kitchenpos.mock.persistence.FakeMenuRepository;
import kitchenpos.mock.persistence.FakeOrderTableRepository;
import kitchenpos.mock.persistence.FakeProductRepository;

public class TestUtil {

    public static final BigDecimal PRICE = BigDecimal.valueOf(28_000);
    public static final BigDecimal MINUS_PRICE = BigDecimal.valueOf(-100);

    private final FakeProductRepository productRepository;
    private final FakeMenuRepository menuRepository;
    private final FakeOrderTableRepository orderTableRepository;
    private final MenuGroup defaultMenuGroup;

    public TestUtil(FakeMenuGroupRepository menuGroupRepository,
        FakeProductRepository productRepository, FakeMenuRepository menuRepository,
        FakeOrderTableRepository orderTableRepository) {
        this.productRepository = productRepository;
        this.defaultMenuGroup = menuGroupRepository.save(MenuGroupFixture.create("추천 메뉴"));
        this.menuRepository = menuRepository;
        this.orderTableRepository = orderTableRepository;
    }

    public MenuGroup getSavedMenuGroup() {
        return defaultMenuGroup;
    }

    public Product getSavedProduct(String name, BigDecimal price) {
        return productRepository.save(ProductFixture.create(name, price));
    }

    public Product getSavedProduct() {
        String name = "치킨";
        BigDecimal price = BigDecimal.valueOf(20000);
        return productRepository.save(ProductFixture.create(name, price));
    }

    public List<MenuProduct> getMenuProducts(int quantity) {
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(MenuProductFixture.create(getSavedProduct(), quantity));
        return menuProducts;
    }

    public List<MenuProduct> getMenuProducts(Product product, int quantity) {
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(MenuProductFixture.create(product, quantity));
        return menuProducts;
    }

    public Menu getSavedMenu() {
        int quantity = 2;
        Menu menu = MenuFixture.create("두마리 치킨", PRICE, true, getSavedMenuGroup(),
            getMenuProducts(quantity));
        return menuRepository.save(menu);
    }

    public Menu getSavedMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public OrderLineItem getOrderLineItem(int quantity) {
        Menu menu = getSavedMenu();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public OrderLineItem getOrderLineItem(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public List<OrderLineItem> getOrderLineItems() {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(getOrderLineItem(1));
        return orderLineItems;
    }

    public OrderTable getSavedOrderTable() {
        OrderTable orderTable = OrderTableFixture.create("1번");
        orderTable.setOccupied(true);
        return orderTableRepository.save(orderTable);
    }
}
