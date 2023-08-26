package kitchenpos.testHelper;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.testHelper.fake.PurgomalumClientFake;
import kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum;
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
    private ProductService productService;

    @Autowired
    private PurgomalumClientFake purgomalumClient;
    private List<Product> products = new ArrayList<>();
    private MenuGroup menuGroup;

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

    protected List<Product> getProducts() {
        return products;
    }

    protected MenuGroup getMenuGroup() {
        if (ObjectUtils.isEmpty(this.menuGroup)) {
            throw new IllegalArgumentException("MenuGroup Init이 된 후 참조가 되어야 합니다.");
        }

        return menuGroup;
    }
}
