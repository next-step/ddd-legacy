//package kitchenpos.application;
//
//import kitchenpos.domain.*;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//class OrderServiceTest {
//
//    @Autowired
//    ProductService productService;
//
//    @Autowired
//    MenuService menuService;
//
//    @Autowired
//    MenuGroupService menuGroupService;
//
//    @Test
//    void create() {
//
//    }
//
//    private Order getOrderRequest() {
//        Menu menu = saveMenu();
//
//        Order request = new Order();
//
//    }
//
//    private Menu saveMenu() {
//        MenuGroup menuGroup = saveMenuGroup();
//        Product product = saveProduct();
//
//        List<MenuProduct> menuProducts = List.of(new MenuProduct(product, 2));
//
//        final String menuName = "순살두마리세트";
//        final BigDecimal menuPrice = BigDecimal.valueOf(39000L);
//        Menu request = new Menu();
//        request.setName(menuName);
//        request.setPrice(menuPrice);
//        request.setMenuGroupId(menuGroup.getId());
//        request.setDisplayed(true);
//        request.setMenuProducts(menuProducts);
//
//        return menuService.create(request);
//    }
//
//    private MenuGroup saveMenuGroup() {
//        final String menuGroupName = "세트류";
//        return menuGroupService.create(new MenuGroup(menuGroupName));
//    }
//
//    private Product saveProduct() {
//        final String productName = "순살치킨";
//        final BigDecimal productPrice = BigDecimal.valueOf(20000L);
//        return productService.create(new Product(productName, productPrice));
//    }
//
//}
//
//
///*
//
//
//    @Test
//    void accept() {
//    }
//
//    @Test
//    void serve() {
//    }
//
//    @Test
//    void startDelivery() {
//    }
//
//    @Test
//    void completeDelivery() {
//    }
//
//    @Test
//    void complete() {
//    }
//
//    @Test
//    void findAll() {
//    }
//
//
//*/
