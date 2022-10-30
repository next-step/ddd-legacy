package kitchenpos.order.application;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.order.domain.*;
import kitchenpos.order.vo.DeliveryAddress;
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

    private Order 매장주문;
    private Order 배달주문;

    @BeforeEach
    void setUp() {
        MenuGroup menuGroup = menuGroupRepository.save(menuGroup(UUID.randomUUID(), "메뉴 그룹명"));
        Product product = productRepository.save(product());
        Menu menu = menuRepository.save(menu(product, menuGroup));
        menu.display();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItem(menu, new Quantity(1)));
        매장주문 = orderRepository.save(매장주문생성(orderLineItems));
        배달주문 = orderRepository.save(배달주문생성(orderLineItems));
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void name() {
        orderStatusService.accept(매장주문.getId());
        assertThatThrownBy(() -> orderStatusService.accept(매장주문.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("WAITING 상태만 접수가능합니다.");
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void accept() {
        orderStatusService.accept(매장주문.getId());
        assertThat(매장주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("접수 상태가 아니면 제공할 수 없다.")
    @Test
    void served() {
        assertThatThrownBy(() -> orderStatusService.serve(매장주문.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACCEPTED 상태만 SERVED 상태로 변경가능합니다");
    }

    @DisplayName("배송을 시작할 수 있다.")
    @Test
    void startDelivery() {
        orderStatusService.accept(배달주문.getId());
        orderStatusService.serve(배달주문.getId());
        orderStatusService.startDelivery(배달주문.getId());
        assertThat(배달주문.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void asdf() {
        orderStatusService.accept(매장주문.getId());
        orderStatusService.serve(매장주문.getId());
        assertThatThrownBy(() -> orderStatusService.startDelivery(매장주문.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있습니다.");
    }

    @DisplayName("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void sdasdf() {
        orderStatusService.accept(배달주문.getId());
        assertThatThrownBy(() -> orderStatusService.startDelivery(배달주문.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.");
    }

    @DisplayName("배송을 완료할 수 있다.")
    @Test
    void sdasdasdff() {
        orderStatusService.accept(배달주문.getId());
        orderStatusService.serve(배달주문.getId());
        orderStatusService.startDelivery(배달주문.getId());
        orderStatusService.completeDelivery(배달주문.getId());
        assertThat(배달주문.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.")
    @Test
    void sdasdasdffasf() {
        orderStatusService.accept(배달주문.getId());
        orderStatusService.serve(배달주문.getId());
        assertThatThrownBy(() -> orderStatusService.completeDelivery(배달주문.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 DELIVERING이 아니면 주문을 완료할 수 없다.");
    }

    private Menu menu(Product product, MenuGroup menuGroup) {
        return new Menu(UUID.randomUUID(), new Name("메뉴명", false), menuGroup, menuProducts(new MenuProduct(product, new Quantity(1))), new Price(BigDecimal.TEN));
    }

    private Product product() {
        return new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN));
    }

    private MenuGroup menuGroup(UUID id, String name) {
        return new MenuGroup(id, new Name(name, false));
    }

    private List<MenuProduct> menuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }

    public Order 배달주문생성(List<OrderLineItem> orderLineItems) {
        return new Order(UUID.randomUUID(), OrderType.DELIVERY, orderLineItems, null, new DeliveryAddress("배송지"));
    }

    public Order 매장주문생성(List<OrderLineItem> orderLineItems) {
        return new Order(UUID.randomUUID(), OrderType.TAKEOUT, orderLineItems, null, null);
    }

}
