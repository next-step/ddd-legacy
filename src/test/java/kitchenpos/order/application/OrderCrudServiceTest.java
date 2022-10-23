package kitchenpos.order.application;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Name;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.menu.menu.application.InMemoryMenuRepository;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.domain.Price;
import kitchenpos.menu.menu.domain.Quantity;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderType;
import kitchenpos.order.dto.OrderLineItemRequest;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.ordertable.domain.NumberOfGuests;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.MenuFixture.createMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 서비스")
class OrderCrudServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private OrderCrudService orderCrudService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        orderCrudService = new OrderCrudService(orderRepository, menuRepository, orderTableRepository);
        menuRepository.save(createMenu(createMenuGroup(UUID.randomUUID(), "메뉴그룹명"), createMenuProducts(new MenuProduct(new Product(new Name("상품명", false), new Price(BigDecimal.TEN)), new Quantity(1L))), new Price(BigDecimal.TEN)));
    }

    @DisplayName("주문 내역을 조회할 수 있다.")
    @Test
    void findOrders() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        OrderTable orderTable = new OrderTable(new Name("테이블명", false), new NumberOfGuests(1));
        orderRepository.save(new Order(OrderType.EAT_IN, orderLineItems, orderTable, null));
        assertThat(orderCrudService.findAll()).hasSize(1);
    }

    @DisplayName("메뉴의 수량과 주문 항목의 수량은 같다.")
    @Test
    void menuSize() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        OrderTable orderTable = new OrderTable(new Name("테이블명", false), new NumberOfGuests(1));
        orderRepository.save(new Order(OrderType.EAT_IN, orderLineItems, orderTable, null));
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(BigDecimal.TEN);
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
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(BigDecimal.TEN);
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
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(BigDecimal.ONE);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격과 메뉴 항목의 가격은 같다.");
    }

    private static List<OrderLineItem> orderLineItems() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = new Menu(menuGroup, createMenuProducts(new MenuProduct(new Product(new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        menu.display();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem(menu);
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
