package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createDeliveryOrder;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class OrderServiceTest extends BaseServiceTest {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final OrderTableRepository orderTableRepository;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    public OrderServiceTest(final OrderService orderService, final OrderRepository orderRepository, final MenuGroupRepository menuGroupRepository, final ProductRepository productRepository, final MenuRepository menuRepository, final OrderTableRepository orderTableRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @DisplayName("주문 요청이 가능하다.")
    @Test
    void test1() {
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID()));
        final Product product = productRepository.save(createProduct(UUID.randomUUID()));
        final MenuProduct menuProduct = createMenuProduct(product);
        final Menu menu = menuRepository.save(menuRepository.save(createMenu(UUID.randomUUID(), menuGroup, true, List.of(menuProduct))));
        final List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(menu));
        final Order order = createDeliveryOrder(orderLineItems);

        final Order createdOrder = orderService.create(order);

        final Order foundOrder = orderRepository.findAll().get(0);

        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getType()).isEqualTo(order.getType());
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(createdOrder.getOrderDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(createdOrder.getOrderLineItems())
                .map(OrderLineItemFields::new)
                .containsExactlyElementsOf(order.getOrderLineItems().stream().map(OrderLineItemFields::new).collect(Collectors.toList()));
        assertThat(createdOrder.getDeliveryAddress()).isEqualTo(order.getDeliveryAddress());
        assertThat(createdOrder.getOrderTable()).isEqualTo(order.getOrderTable());
        assertThat(createdOrder.getOrderTableId()).isEqualTo(order.getOrderTableId());
        assertThat(foundOrder.getId()).isEqualTo(createdOrder.getId());
    }

    @DisplayName("주문의 수령 방법은 필수이다.")
    @Test
    void test2() {
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID()));
        final Product product = productRepository.save(createProduct(UUID.randomUUID()));
        final MenuProduct menuProduct = createMenuProduct(product);
        final Menu menu = menuRepository.save(menuRepository.save(createMenu(UUID.randomUUID(), menuGroup, true, List.of(menuProduct))));
        final List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(menu));
        final Order order = createOrder(null, null, orderLineItems, null);

        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문의 주문 목록은 비어있으면 안된다")
    @Test
    void test3() {
        final Order order = createOrder(OrderType.DELIVERY, "delivery", null, null);

        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문의 주문 목록은 필수이다")
    @Test
    void test4() {
        final Order order = createOrder(OrderType.DELIVERY, "delivery", Collections.emptyList(), null);

        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    private static class OrderLineItemFields {
        final Menu menu;
        final long quantity;

        public OrderLineItemFields(final OrderLineItem orderLineItem) {
            this.menu = orderLineItem.getMenu();
            this.quantity = orderLineItem.getQuantity();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final OrderLineItemFields that = (OrderLineItemFields) o;
            return quantity == that.quantity && Objects.equals(menu, that.menu);
        }

        @Override
        public int hashCode() {
            return Objects.hash(menu, quantity);
        }
    }
}