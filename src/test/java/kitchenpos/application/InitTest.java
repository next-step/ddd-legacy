package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public abstract class InitTest {
    @Resource
    protected MenuGroupRepository menuGroupRepository;
    @Resource
    protected ProductRepository productRepository;
    @Resource
    protected MenuRepository menuRepository;
    @Resource
    protected OrderTableRepository orderTableRepository;
    @Resource
    protected OrderRepository orderRepository;

    protected static final UUID MENU_GROUP_ID = UUID.randomUUID();
    protected static final UUID PRODUCT_ID = UUID.randomUUID();
    protected static final UUID INVALID_ID = UUID.randomUUID();
    protected static final UUID MENU_ID = UUID.randomUUID();
    protected static final UUID UNDISPLAYED_MENU_ID = UUID.randomUUID();
    protected static final UUID ORDER_TABLE_ID = UUID.randomUUID();
    protected static final UUID OCCUPIED_ORDER_TABLE_ID = UUID.randomUUID();
    protected static final UUID ORDER_ID = UUID.randomUUID();


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

    protected Order buildValidTakeoutDelivery() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(buildValidOrderLineItem()));

        return order;
    }

    protected Order buildValidDeliveryOrder() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(buildValidOrderLineItem()));
        order.setDeliveryAddress("강서구청");

        return order;
    }

    protected Order buildValidEatInOrder() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(List.of(buildValidOrderLineItem()));
        order.setOrderTableId(ORDER_TABLE_ID);
        order.setOrderTable(buildValidOrderTable());
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());

        return order;
    }

    protected OrderLineItem buildValidOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(MENU_ID);
        orderLineItem.setMenu(buildValidMenu());
        orderLineItem.setPrice(BigDecimal.ONE);

        return orderLineItem;
    }

    protected OrderTable buildValidOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setName("4인테이블A");
        orderTable.setOccupied(false);

        return orderTable;
    }

    protected OrderTable buildOccupiedOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(OCCUPIED_ORDER_TABLE_ID);
        orderTable.setName("2인테이블A");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(1);

        return orderTable;
    }

    @BeforeEach
    void initTest() {
        menuGroupRepository.save(buildValidMenuGroup());
        productRepository.save(buildValidProduct());
        menuRepository.save(buildValidMenu());
        menuRepository.save(buildUndisplayedMenu());
        orderTableRepository.save(buildValidOrderTable());
        orderTableRepository.save(buildOccupiedOrderTable());
        orderRepository.save(buildValidEatInOrder());
    }
}
