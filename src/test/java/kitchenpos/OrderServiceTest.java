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

    // 주문 공통

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
    @DisplayName("주문을 수락할 수 있다.")
    public void accept() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setStatus(OrderStatus.WAITING);
        Order request = orderRepository.save(order);

        Order actual = orderService.accept(request.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문 수락 시, 주문 상태가 대기 중이어야 한다.")
    public void accept_waiting() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        Order request = orderRepository.save(order);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(request.getId()));
    }

    @Test
    @DisplayName("주문을 제공한다.")
    public void serve() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setStatus(OrderStatus.ACCEPTED);
        Order request = orderRepository.save(order);

        Order actual = orderService.serve(request.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문 제공 시, 주문 상태가 '수락' 이어야 한다.")
    public void accept_accepted() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        Order request = orderRepository.save(order);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(request.getId()));
    }

    @Test
    @DisplayName("주문 목록을 조회할 수 있다.")
    public void findAll() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        orderRepository.save(order);

        List<Order> actual = orderService.findAll();

        assertThat(actual).hasSize(1);
    }


    // 배달 주문
    @Test
    @DisplayName("배달 주문 시, 주문하는 메뉴의 수량이 존재해야한다.")
    public void create_quantity_delivery() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true), 0)));
        order.setDeliveryAddress("집주소");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }
    @Test
    @DisplayName("배달 주문 시, 주소는 필수로 입력되어야 한다.")
    public void order_delivery_input_address() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @Test
    @DisplayName("배달 주문 수락 시, 라이더 배달 요청을 한다.")
    public void accept_request_delivery() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");
        Order request = orderService.create(order);
        BigDecimal price = getPrice(order);

        ridersClient.requestDelivery(request.getId(), price, order.getDeliveryAddress());

        // 외부 모듈의 경우 어떻게 테스트를..?
    }

    @Test
    @DisplayName("주문이 제공되면, 라이더가 배달을 시작한다.")
    public void delivery() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());
        Order serve = orderService.serve(accept.getId());

        Order actual = orderService.startDelivery(serve.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달 시, 주문한 메뉴가 준비되어있어야 배달이 가능하다.")
    public void delivery_serve() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(accept.getId()));
    }

    @Test
    @DisplayName("배달이 끝나면 배달을 완료할 수 있다.")
    public void delivery_complete_delivery() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());
        Order serve = orderService.serve(accept.getId());
        Order delivery = orderService.startDelivery(serve.getId());

        Order actual = orderService.completeDelivery(delivery.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배달 주문을 완료할 수 있다.")
    public void delivery_complete() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());
        Order serve = orderService.serve(accept.getId());
        Order startDelivery = orderService.startDelivery(serve.getId());
        Order completeDelivery = orderService.completeDelivery(startDelivery.getId());

        Order actual = orderService.complete(completeDelivery.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("아직 주문의 상태가 배달중이라면, 주문을 완료할 수 없다.")
    public void delivery_still_delivering() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));
        order.setDeliveryAddress("집주소");

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());
        Order serve = orderService.serve(accept.getId());
        Order startDelivery = orderService.startDelivery(serve.getId());

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(startDelivery.getId()));
    }

    // 포장 주문
    @Test
    @DisplayName("포장 주문 시, 주문하는 메뉴의 수량이 존재해야한다.")
    public void create_quantity_takeout() {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true), 0)));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
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
    @DisplayName("포장 주문을 완료한다.")
    public void order_takeout_complete() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());
        Order serve = orderService.serve(accept.getId());

        Order actual = orderService.complete(serve.getId());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("포장 주문 시, 메뉴가 제공돼야 주문을 완료할 수 있다.")
    public void order_takeout_still_accept() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(OrderLineItemFactory.of(createMenuAndSave(true))));

        Order create = orderService.create(order);
        Order accept = orderService.accept(create.getId());

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(accept.getId()));
    }


    private BigDecimal getPrice(Order order) {
        BigDecimal sum = BigDecimal.ZERO;
        for (final OrderLineItem orderLineItem : order.getOrderLineItems()) {
            sum = orderLineItem.getMenu()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(orderLineItem.getQuantity()));
        }
        return sum;
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
