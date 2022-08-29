package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public abstract class InitTest {
    @Resource
    protected MenuGroupRepository menuGroupRepository;
    @Resource
    protected ProductRepository productRepository;
    @Resource
    protected MenuRepository menuRepository;

    protected static final UUID MENU_GROUP_ID = UUID.randomUUID();
    protected static final UUID PRODUCT_ID = UUID.randomUUID();
    protected static final UUID INVALID_ID = UUID.randomUUID();
    protected static final UUID MENU_ID = UUID.randomUUID();
    protected static final UUID UNDISPLAYED_MENU_ID = UUID.randomUUID();


    protected MenuGroup buildValidMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("치킨류");

        return menuGroup;
    }

    protected Product buildValidProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("양념치킨");
        product.setPrice(BigDecimal.ONE);

        return product;
    }


    protected MenuProduct buildValidMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ID);
        menuProduct.setProduct(buildValidProduct());
        menuProduct.setQuantity(1L);

        return menuProduct;
    }

    protected Menu buildValidMenu() {
        Menu menu = new Menu();
        menu.setId(MENU_ID);
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroupId(MENU_GROUP_ID);
        menu.setMenuGroup(buildValidMenuGroup());
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(buildValidMenuProduct()));
        menu.setName("양념치킨메뉴");

        return menu;
    }

    protected Menu buildUndisplayedMenu() {
        Menu menu = new Menu();
        menu.setId(UNDISPLAYED_MENU_ID);
        menu.setPrice(BigDecimal.TEN);
        menu.setMenuGroupId(MENU_GROUP_ID);
        menu.setMenuGroup(buildValidMenuGroup());
        menu.setDisplayed(false);
        menu.setMenuProducts(List.of(buildValidMenuProduct()));
        menu.setName("디피안된메뉴");

        return menu;
    }

    @BeforeEach
    void initTest() {
        menuGroupRepository.save(buildValidMenuGroup());
        productRepository.save(buildValidProduct());
        menuRepository.save(buildValidMenu());
        menuRepository.save(buildUndisplayedMenu());
    }
}
