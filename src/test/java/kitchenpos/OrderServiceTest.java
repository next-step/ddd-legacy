package kitchenpos;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.factory.*;
import kitchenpos.fakeobject.*;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.infra.RidersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private MenuRepository menuRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;
    private OrderRepository orderRepository;
    private OrderTableRepository orderTableRepository;
    private RidersClient ridersClient;
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        menuRepository = new InMemoryMenuRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        ridersClient = new FakeKitchenridersClient();

        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, ridersClient);
    }

    @Test
    @DisplayName("배달 주문을 생성한다.")
    public void order_delivery() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");

        Order actual = orderService.create(order);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("포장 주문을 생성한다.")
    public void order_takeout() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));

        Order actual = orderService.create(order);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("주문 시, 주문 타입과 수량은 필수로 입력되어야 한다.")
    public void order_input_null() {
        Order order = new Order();
        order.setDeliveryAddress("집주소");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @Test
    @DisplayName("주문 시, 존재 하지 않는 메뉴는 주문할 수 없다.")
    public void order_not_exist_menu() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(new Menu())));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @Test
    @DisplayName("주문 시, 진열 되지않은 메뉴는 주문할 수 없다.")
    public void order_not_displayed() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(false))));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @Test
    @DisplayName("주문 시, 메뉴의 가격과 요청된 주문의 가격이 다르면 주문할 수 없다.")
    public void order_different_price() {
        Menu request = createMenuAndSave(true);

        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(request)));
        request.setPrice(BigDecimal.valueOf(10000L));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }


    @Test
    @DisplayName("매장 주문을 생성한다.")
    public void order_eat_in() {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setOrderTable(new OrderTable());

        Order actual = orderService.create(order);
        assertThat(actual.getId()).isNotNull();
    }

    private Menu createMenuAndSave(boolean displayed) {
        Menu menu = createMenu(createMenuProduct(1), displayed);
        return menuRepository.save(menu);
    }

    private MenuGroup createMenuGroup() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);
        return createMenuGroup;
    }

    private Menu createMenu(List<MenuProduct> MenuProduct, boolean displayed) {
        final MenuGroup createMenuGroup = createMenuGroup();
        final List<MenuProduct> menuProducts = MenuProduct;
        final Menu request = MenuFactory.getDefaultMenu(createMenuGroup, menuProducts, displayed);
        return request;
    }

    private List<MenuProduct> createMenuProduct(int quantity) {
        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));
        return List.of(MenuProductFactory.of(황금올리브, quantity), MenuProductFactory.of(호가든, quantity));
    }
}
