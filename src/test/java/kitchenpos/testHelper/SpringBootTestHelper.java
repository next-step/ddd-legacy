package kitchenpos.testHelper;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.testHelper.fake.PurgomalumClientFake;
import kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum;
import kitchenpos.testHelper.fixture.MenuFixture;
import kitchenpos.testHelper.fixture.OrderTableFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ObjectUtils;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest(properties = {
    "spring.config.location =" + "classpath:/application-test.properties"})
@Component
public abstract class SpringBootTestHelper {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private MenuGroupService menuGroupService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private PurgomalumClientFake purgomalumClient;
    private List<Product> products = new ArrayList<>();
    private List<Menu> menus = new ArrayList<>();
    private MenuGroup menuGroup;
    private OrderTable orderTable;

    public void init() {
        databaseCleanup.execute();
    }


    protected void initMenuGroup() {
        this.menuGroup = menuGroupService.create(new MenuGroup("메뉴그룹"));
    }

    protected void initProduct() {
        purgomalumClient.setReturn(Purgomalum.NORMAL);
        List<Product> requests = List.of(
            new Product("P1", BigDecimal.valueOf(1000L)),
            new Product("P2", BigDecimal.valueOf(2000L)),
            new Product("P3", BigDecimal.valueOf(3000L))
        );
        for (Product request : requests) {
            productService.create(request);
        }

        this.products = productService.findAll();
    }

    protected void initOrderTable() {
        this.orderTable = orderTableService.create(OrderTableFixture.createRequestBuilder()
            .name("orderTable")
            .build()
        );
        orderTableService.sit(this.orderTable.getId());
    }

    protected void initMenu() {
        Menu menu1 = menuService.create(
            MenuFixture.createRequestBuilder()
                .menuGroupId(menuGroup.getId())
                .menuProduct(products.get(0), 1L)
                .menuProduct(products.get(1), 1L)
                .isDisplay(true)
                .name("menuName")
                .build()
        );

        Menu menu2 = menuService.create(
            MenuFixture.createRequestBuilder()
                .menuGroupId(menuGroup.getId())
                .menuProduct(products.get(1), 1L)
                .menuProduct(products.get(2), 1L)
                .name("menuName")
                .isDisplay(true)
                .build()
        );

        this.menus = List.of(menu1, menu2);
    }

    protected List<Product> getProducts() {
        return products;
    }

    protected MenuGroup getMenuGroup() {
        if (ObjectUtils.isEmpty(this.menuGroup)) {
            throw new IllegalArgumentException("MenuGroup Init이 된 후 참조가 되어야 합니다.");
        }

        return menuGroup;
    }

    protected OrderTable getOrderTable() {
        if (ObjectUtils.isEmpty(this.orderTable)) {
            throw new IllegalArgumentException("OrderTable Init이 된 후 참조가 되어야 합니다.");
        }

        return orderTable;
    }

    public List<Menu> getMenus() {
        return menus;
    }
}
