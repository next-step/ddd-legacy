package kitchenpos.order.application;

import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.application.MenuService;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderType;
import kitchenpos.order.dto.request.OrderLineItemRequest;
import kitchenpos.order.dto.request.OrderRequest;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;
import kitchenpos.ordertable.vo.NumberOfGuests;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.menu.domain.MenuFixture.createMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 서비스")
@SpringBootTest
@Transactional
class OrderCrudServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderCrudService orderCrudService;

    @Autowired
    private PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    private Menu menu;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable(UUID.randomUUID(), new Name("테이블명", false), new NumberOfGuests(1));
        orderCrudService = new OrderCrudService(orderRepository, menuRepository, orderTableRepository);
        orderTableRepository.save(orderTable);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        Product product = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID(), "메뉴그룹명"));
        menu = menuRepository.save(createMenu(menuGroup, new Name("메뉴명", false), createMenuProducts(new MenuProduct(product, new Quantity(1L))), new Price(BigDecimal.TEN)));
    }

    @DisplayName("주문 내역을 조회할 수 있다.")
    @Test
    void findOrders() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        orderRepository.save(new Order(UUID.randomUUID(), OrderType.EAT_IN, orderLineItems, orderTable, null));
        assertThat(orderCrudService.findAll()).hasSize(1);
    }

    @DisplayName("메뉴의 수량과 주문 항목의 수량은 같다.")
    @Test
    void menuSize() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        orderRepository.save(new Order(UUID.randomUUID(), OrderType.EAT_IN, orderLineItems, orderTable, null));
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 수량과 주문 항목의 수량은 같다.");
    }

    @DisplayName("주문 타입은 배송 / 포장 / 매장 중 한 가지를 갖는다.")
    @Test
    void orderType() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, null);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 타입을 입력해주세요.");
    }

    @DisplayName("메뉴의 가격과 메뉴 항목의 가격은 같다.")
    @Test
    void validatePrice() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.ONE, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격과 메뉴 항목의 가격은 같다.");
    }

    @DisplayName("주문 항목은 비어 있을 수 없다.")
    @Test
    void orderLineItemsNotNull() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 항목은 비어 있을 수 없습니다.");
    }

    @DisplayName("주문 타입은 필수값으로 입력받는다.")
    @Test
    void validateType() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, null);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 타입을 입력해주세요.");
    }

    @DisplayName("매장 주문이 아닐 경우 수량은 0개보다 적을 수 없다.")
    @Test
    void asdf() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, -1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 주문이 아닐 경우 수량은 0개보다 적을 수 없다.");
    }

    @DisplayName("안보이는 메뉴가 주문될 수 없다.")
    @Test
    void asdfdsf() {
        menuService.hide(menu.getId());
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("안보이는 메뉴가 주문될 수 없다.");
    }

    private static List<OrderLineItem> orderLineItems() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = new Menu(UUID.randomUUID(), new Name("메뉴명", false), menuGroup, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        menu.display();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem(menu, new Quantity(1));
        orderLineItems.add(orderLineItem);
        return orderLineItems;
    }

    private static MenuGroup createMenuGroup(UUID id, String menuGroupName) {
        return new MenuGroup(id, new Name(menuGroupName, false));
    }

    private static List<MenuProduct> createMenuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }
}
