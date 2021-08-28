package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.infra.MockKitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final KitchenridersClient kitchenridersClient = new MockKitchenridersClient();

    @Spy
    private Order orderRequest = orderRequest(3L, BigDecimal.valueOf(19000), true);

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("주문 생성")
    @Test
    void create() {
        Order actual = orderRequest;
        Order expected = orderService.create(actual);

        assertThat(expected).isNotNull();
    }

    @DisplayName("주문 생성 - 주문의 종류로 배달(DELIVERY), 테이크아웃(TAKEOUT), 홀이용(EAT_IN) 중 하나를 반드시 선택해야한다.")
    @Test
    void createValidationOrderType() {
        Order actual = orderRequest;
        when(actual.getType()).thenReturn(null);

        assertThatThrownBy(() -> orderService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성 - 주문에는 이미 등록된 메뉴가 포함되어 있어야 한다.")
    @Test
    void createValidationOrderLineItems() {
        Order actual = orderRequest;

        // null OrderLineItems
        when(actual.getOrderLineItems()).thenReturn(null);
        assertThatThrownBy(() -> orderService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);

        // empty OrderLineItems
        when(actual.getOrderLineItems()).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> orderService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성 - 주문의 종류가 배달 혹은 테이크아웃인 경우 주문을 구성하는 각 메뉴의 수량은 1개 이상이어야 한다.")
    @Test
    void createValidationOrderQuantity() {
        Order actual = spy(orderRequest(-3L, BigDecimal.valueOf(19000), true));

        // DELIVERY
        when(actual.getType()).thenReturn(OrderType.DELIVERY);
        assertThatThrownBy(() -> orderService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);

        // TAKEOUT
        when(actual.getType()).thenReturn(OrderType.TAKEOUT);
        assertThatThrownBy(() -> orderService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성 - 주문에 포함된 메뉴는 제공 가능한 메뉴여야 한다.(노출되어 있는 메뉴여야 한다)")
    @Test
    void createValidationOrderMenu() {
        Order notExistsMenu = spy(orderRequest(3L, BigDecimal.valueOf(19000), true));

        // not exists menu
        when(menuRepository.findById(
                notExistsMenu.getOrderLineItems()
                        .stream()
                        .findFirst()
                        .get().getMenuId())).thenThrow(NoSuchElementException.class);
        assertThatThrownBy(() -> orderService.create(notExistsMenu))
                .isInstanceOf(NoSuchElementException.class);

        // menu display false
        Order invisibleMenu = spy(orderRequest(3L, BigDecimal.valueOf(19000), false));
        assertThatThrownBy(() -> orderService.create(invisibleMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성 - 주문의 종류가 배달인 경우 반드시 주소를 입력해야한다.")
    @Test
    void createValidationAddress() {

        // null address
        Order nullAddress = orderRequest;
        when(nullAddress.getType()).thenReturn(OrderType.DELIVERY);
        when(nullAddress.getDeliveryAddress()).thenReturn(null);
        assertThatThrownBy(() -> orderService.create(nullAddress))
                .isInstanceOf(IllegalArgumentException.class);

        // empty address
        Order emptyAddress = orderRequest;
        when(emptyAddress.getType()).thenReturn(OrderType.DELIVERY);
        when(emptyAddress.getDeliveryAddress()).thenReturn("");
        assertThatThrownBy(() -> orderService.create(emptyAddress))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("주문 생성 - 주문의 종류가 홀이용인 경우 이용할 테이블 식별값을 입력해야한다.")
    @Test
    void createValidationTableNotExists() {

        Order notExistsTable = orderRequest;
        when(notExistsTable.getType()).thenReturn(OrderType.EAT_IN);
        when(orderRepository.findById(notExistsTable.getOrderTableId())).thenThrow(NoSuchElementException.class);
        assertThatThrownBy(() -> orderService.create(notExistsTable))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("주문 수락 - 주문이 수락되면 주문의 상태는 ACCEPTED(수락됨) 으로 변경된다.")
    @Test
    void accept() {
        Order actual = orderService.create(orderRequest);
        Order expected = orderService.accept(actual.getId());

        assertThat(expected.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 수락 - 존재하지 않는 주문에 대한 수락을 할 수 없다.")
    @Test
    void acceptValidationNotExists() {
        Order actual = spy(orderService.create(orderRequest));

        when(orderRepository.findById(actual.getId())).thenThrow(NoSuchElementException.class);
        assertThatThrownBy(() -> orderService.accept(actual.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 수락 - 주문 수락 시점에 주문의 상태가 WAITING(대기) 상태여야 한다.")
    @Test
    void acceptValidationNotStatus() {
        Order actual = orderService.create(orderRequest);
        actual.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(actual);

        assertThatThrownBy(() -> orderService.accept(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 서빙 - 주문에 대한 서빙이 발생하면 해당 주문의 상태는 SERVED(서빙됨) 로 변경된다.")
    @Test
    void serve() {
        Order actual = orderRequest;
        actual.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(actual);

        Order expected = orderService.serve(actual.getId());
        assertThat(expected.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 서빙 - 서빙 시점에 주문의 상태가 ACCEPTED(수락됨) 상태여야 한다.")
    @Test
    void serveValidationStatus() {
        Order actual = orderRequest;
        actual.setStatus(OrderStatus.SERVED);
        orderRepository.save(actual);

        assertThatThrownBy(() -> orderService.serve(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달 시작 - 주문에 대한 배달이 시작되면 주문의 상태가 DELIVERING(배달중) 으로 변경된다.")
    @Test
    void startDelivery() {
        Order actual = orderService.create(orderRequest);
        actual.setType(OrderType.DELIVERY);
        actual.setStatus(OrderStatus.SERVED);
        orderRepository.save(actual);

        Order expected = orderService.startDelivery(actual.getId());
        assertThat(expected.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 배달 시작 - 주문의 종류가 DELIVERY(배달)이어야 한다.")
    @Test
    void startDeliveryValidationStatusDelivery() {
        Order actual = orderRequest;
        actual.setType(OrderType.EAT_IN);
        orderRepository.save(actual);

        assertThatThrownBy(() -> orderService.startDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달 시작 - 배달 시작 시점에 주문의 상태가 SERVED(서빙됨) 상태이면 안된다.")
    @Test
    void startDeliveryValidationStatusServed() {
        Order actual = orderRequest;
        actual.setStatus(OrderStatus.SERVED);
        orderRepository.save(actual);

        assertThatThrownBy(() -> orderService.startDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달 완료 - 주문에 대한 배달이 완료되면 주문의 상태가 DELIVERED(배달됨) 으로 변경된다.")
    @Test
    void completeDelivery() {
        Order actual = orderRequest;
        actual.setType(OrderType.DELIVERY);
        actual.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(actual);

        Order expected = orderService.completeDelivery(actual.getId());
        assertThat(expected.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 배달 완료 - 주문의 종류가 DELIVERY(배달)이어야 한다.")
    @Test
    void completeDeliveryValidationOrderType() {
        Order actual = orderRequest;
        actual.setType(OrderType.EAT_IN);
        orderRepository.save(actual);

        assertThatThrownBy(() -> orderService.completeDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달 완료 - 배달 완료 시점에 주문의 상태가 DELIVERING(배달중) 상태여야 한다.")
    @Test
    void completeDeliveryValidationOrderStatus() {
        Order actual = orderRequest;
        actual.setType(OrderType.DELIVERY);
        actual.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(actual);

        assertThatThrownBy(() -> orderService.completeDelivery(actual.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료 - 주문 완료 시점에 주문의 상태가 COMPLETED(완료됨) 로 변경된다.")
    @Test
    void complete() {
        Order actual = orderRequest;
        actual.setType(OrderType.DELIVERY);
        actual.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(actual);

        Order expected = orderService.complete(actual.getId());
        assertThat(expected.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 완료 - 주문 종류가 DELIVERY(배달) 이면서, 배달된 주문(주문의 상태가 DELIVERED(배달됨)인 주문)이 아닌 것은 완료 처리할 수 없다.")
    @Test
    void completeValidationOrderStatus() {
        Order actual = orderRequest;
        actual.setType(OrderType.DELIVERY);
        actual.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(actual);

        Order expected = orderService.complete(actual.getId());
        assertThat(expected.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 완료 - 주문의 종류가 TAKEOUT(테이크아웃) 또는 EAT_IN(홀이용) 인데 아직 서빙이 되지 않은(주문의 상태가 SERVED가 아닌) 경우 해당 주문을 완료 처리할 수 없다.")
    @Test
    void completeValidationOrderType() {
        // TAKEOUT
        Order takeOut = orderRequest;
        takeOut.setType(OrderType.TAKEOUT);
        takeOut.setStatus(OrderStatus.WAITING);
        orderRepository.save(takeOut);
        assertThatThrownBy(() -> orderService.complete(takeOut.getId()))
                .isInstanceOf(IllegalStateException.class);

        // EAT_IN
        Order eatIn = orderRequest;
        eatIn.setType(OrderType.EAT_IN);
        eatIn.setStatus(OrderStatus.WAITING);
        orderRepository.save(eatIn);
        assertThatThrownBy(() -> orderService.complete(eatIn.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("모든 주문 조회")
    @Test
    void findAll() {
        orderService.create(orderRequest);
        orderService.create(orderRequest);
        assertThat(orderRepository.findAll().size())
                .isEqualTo(orderService.findAll().size());
    }

    private Order orderRequest(Long orderItemQuantity, BigDecimal menuPrice, Boolean isDisplayed) {
        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);

        OrderTable orderTable = orderTableRepository.save(new OrderTable());
        orderRequest.setOrderTableId(orderTable.getId());
        orderRequest.setOrderLineItems(getOrderLineItems(orderItemQuantity, menuPrice, isDisplayed));
        return orderRequest;
    }

    private List<OrderLineItem> getOrderLineItems(Long orderItemQuantity, BigDecimal menuPrice, Boolean isDisplayed) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem(orderItemQuantity, menuPrice, isDisplayed));
        return orderLineItems;
    }

    private OrderLineItem orderLineItem(Long orderItemQuantity, BigDecimal menuPrice, Boolean isDisplayed) {
        Menu menu = menuRepository.save(menuRequest(menuPrice));
        menu.setDisplayed(isDisplayed);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setQuantity(orderItemQuantity);
        return orderLineItem;
    }

    private Menu menuRequest(BigDecimal menuPrice) {
        Menu menuRequest = new Menu();
        menuRequest.setName("후라이드+후라이드");
        menuRequest.setPrice(menuPrice);
        menuRequest.setMenuGroupId(saveMenuGroup().getId());
        menuRequest.setDisplayed(true);

        menuRequest.setMenuProducts(menuProducts());
        return menuRequest;
    }

    private MenuGroup saveMenuGroup() {
        return menuGroupRepository.save(new MenuGroup());
    }

    private List<MenuProduct> menuProducts() {
        List<MenuProduct> menuProducts = new ArrayList<>();
        Product product = productRepository.save(new Product("후라이드", BigDecimal.valueOf(10000)));
        menuProducts.add(new MenuProduct(product, 2));
        return menuProducts;
    }

}