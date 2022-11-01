package kitchenpos.order.application;

import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.MenuFixture;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menugroup.MenuGroupFixture;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderStatus;
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

import static kitchenpos.order.OrderRequestFixture.매장주문생성;
import static kitchenpos.order.OrderRequestFixture.배달주문생성;
import static kitchenpos.product.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@DisplayName("주문 상태")
class OrderStatusServiceTest {

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Order eatInOrder;
    private Order deliveryOrder;

    @BeforeEach
    void setUp() {
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup(UUID.randomUUID()));
        Product product = productRepository.save(product(UUID.randomUUID(), BigDecimal.ONE));
        Menu menu = menuRepository.save(MenuFixture.menu(menuGroup, MenuFixture.menuProducts(product.getId())));
        menu.display();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItem(menu, new Quantity(1)));
        eatInOrder = orderRepository.save(매장주문생성(orderLineItems));
        deliveryOrder = orderRepository.save(배달주문생성(orderLineItems));
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void name() {
        orderStatusService.accept(eatInOrder.getId());
        assertThatThrownBy(() -> orderStatusService.accept(eatInOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("WAITING 상태만 접수가능합니다.");
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void accept() {
        orderStatusService.accept(eatInOrder.getId());
        assertThat(eatInOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("접수 상태가 아니면 제공할 수 없다.")
    @Test
    void served() {
        assertThatThrownBy(() -> orderStatusService.serve(eatInOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACCEPTED 상태만 SERVED 상태로 변경가능합니다");
    }

    @DisplayName("배송을 시작할 수 있다.")
    @Test
    void startDelivery() {
        orderStatusService.accept(deliveryOrder.getId());
        orderStatusService.serve(deliveryOrder.getId());
        orderStatusService.startDelivery(deliveryOrder.getId());
        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void asdf() {
        orderStatusService.accept(eatInOrder.getId());
        orderStatusService.serve(eatInOrder.getId());
        assertThatThrownBy(() -> orderStatusService.startDelivery(eatInOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있습니다.");
    }

    @DisplayName("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void sdasdf() {
        orderStatusService.accept(deliveryOrder.getId());
        assertThatThrownBy(() -> orderStatusService.startDelivery(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.");
    }

    @DisplayName("배송을 완료할 수 있다.")
    @Test
    void sdasdasdff() {
        orderStatusService.accept(deliveryOrder.getId());
        orderStatusService.serve(deliveryOrder.getId());
        orderStatusService.startDelivery(deliveryOrder.getId());
        orderStatusService.completeDelivery(deliveryOrder.getId());
        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.")
    @Test
    void sdasdasdffasf() {
        orderStatusService.accept(deliveryOrder.getId());
        orderStatusService.serve(deliveryOrder.getId());
        assertThatThrownBy(() -> orderStatusService.completeDelivery(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.");
    }

    @DisplayName("주문을 완료할 수 있다.")
    @Test
    void asdasdsdasdasdffasf() {
        orderStatusService.accept(eatInOrder.getId());
        orderStatusService.serve(eatInOrder.getId());
        orderStatusService.complete(eatInOrder.getId());
        assertThat(eatInOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 타입이 DELIVERY이고 주문 상태가 DELIVERED가 아니면 주문을 완료할 수 없다.")
    @Test
    void asdasdsdasdasdfasdfasf() {
        orderStatusService.accept(deliveryOrder.getId());
        assertThatThrownBy(() -> orderStatusService.complete(deliveryOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 DELIVERED가 아니면 주문을 완료할 수 없다.");
    }

    @DisplayName("주문 타입이 TAKEOUT 또는 EAT_IN이고 주문상태가 SERVED아니면 주문을 완료할 수 없다.")
    @Test
    void asdasdsdasdasf() {
        orderStatusService.accept(eatInOrder.getId());
        assertThatThrownBy(() -> orderStatusService.complete(eatInOrder.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 SERVED가 아니면 주문을 완료할 수 없다.");
    }
}
