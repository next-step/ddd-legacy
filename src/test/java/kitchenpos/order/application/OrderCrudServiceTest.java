package kitchenpos.order.application;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.MenuFixture;
import kitchenpos.menu.menu.application.MenuDisplayService;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menugroup.MenuGroupFixture;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.order.OrderFixture;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderType;
import kitchenpos.order.dto.request.OrderLineItemRequest;
import kitchenpos.order.dto.request.OrderRequest;
import kitchenpos.ordertable.application.OrderTableService;
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
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.menu.MenuFixture.createMenu;
import static kitchenpos.order.OrderRequestFixture.*;
import static org.assertj.core.api.Assertions.*;

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
    private MenuDisplayService menuDisplayService;

    @Autowired
    private OrderTableService orderTableService;

    private Menu menu;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable(UUID.randomUUID(), new Name("테이블명", false), new NumberOfGuests(1));
        orderTableRepository.save(orderTable);
        Product product = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup(UUID.randomUUID()));
        menu = menuRepository.save(createMenu(menuGroup, new Name("메뉴명", false), MenuFixture.menuProducts(product.getId()), new Price(BigDecimal.TEN)));
    }

    @DisplayName("주문 내역을 조회할 수 있다.")
    @Test
    void findOrders() {
        List<OrderLineItem> orderLineItems = OrderFixture.orderLineItems(menu);
        orderRepository.save(OrderFixture.order(orderLineItems, orderTable));
        assertThat(orderCrudService.findAll()).hasSize(1);
    }

    @DisplayName("메뉴의 수량과 주문 항목의 수량은 같다.")
    @Test
    void menuSize() {
        assertThatThrownBy(() -> orderCrudService.create(메뉴수량_주문항목수량_다름(menu, orderTable)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 수량과 주문 항목의 수량은 같다.");
    }


    @DisplayName("주문 타입은 배송 / 포장 / 매장 중 한 가지를 갖는다.")
    @Test
    void orderType() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, null, "주소", orderTable.getId());
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
        OrderRequest orderRequest = 메뉴가격_메뉴항목_가격_다름(menu, orderTable);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격과 메뉴 항목의 가격은 같다.");
    }

    @DisplayName("주문 항목은 비어 있을 수 없다.")
    @Test
    void orderLineItemsNotNull() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderRequest orderRequest = 주문항목_비어있음(orderTable);
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
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, null, "주소", orderTable.getId());
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
        OrderRequest orderRequest = 포장주문_수량0개미만(menu, orderTable);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 주문이 아닐 경우 수량은 0개보다 적을 수 없다.");
    }


    @DisplayName("안보이는 메뉴가 주문될 수 없다.")
    @Test
    void asdfdsf() {
        menuDisplayService.hide(menu.getId());
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = 메뉴수량_주문항목수량_같음(menu, orderTable);
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("안보이는 메뉴가 주문될 수 없다.");
    }

    @DisplayName("배달 주문이면 배송지가 없을 수 없다.")
    @Test
    void delivery() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.DELIVERY, null, orderTable.getId());
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.TAKEOUT, null, orderTable.getId());
        assertThatNoException().isThrownBy(() -> orderCrudService.create(orderRequest));
    }

    @DisplayName("매장 주문에서 착석된 테이블을 선택할 수 없다.")
    @Test
    void createdf() {
        orderTableService.sit(orderTable.getId());
        final List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();
        OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), BigDecimal.TEN, 1);
        orderLineItemRequests.add(orderLineItemRequest);
        OrderRequest orderRequest = new OrderRequest(orderLineItemRequests, OrderType.EAT_IN, null, orderTable.getId());
        assertThatThrownBy(() -> orderCrudService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 주문에서 착석된 테이블을 선택할 수 없다.");
    }


}
