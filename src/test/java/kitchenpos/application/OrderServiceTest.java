package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderServiceTest {
    @SpyBean
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("주문 타입 정보가 없을 경우 주문 실패")
    @Test
    public void create_order_type_null() {
        //given
        Order order = makeRandomOrder();
        order.setType(null);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("주문의 메뉴 주문 정보가 없을 경우 주문 실패")
    @Test
    public void create_order_line_item_null() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(null);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("주문의 메뉴가 존재하지 않을 경우 주문 실패")
    @Test
    public void create_order_line_item_not_exist_menu() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(makeNonExistOrderLineItems());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("주문이 먹고 가기가 아니고, 주문 수량이 0 미만일 경우 주문 실패")
    @Test
    public void create_order_not_eat_in_and_quantity_minus() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(makeExistAndInvalidQuantityOrderLineItems());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("주문의 메뉴가 전시상태가 아닐 경우 주문 실패")
    @Test
    public void create_menu_not_displayed() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(makeExistAndNonDisplayMenuOrderLineItems());

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.create(order));
    }

    @DisplayName("주문의 금액과 메뉴 금액이 다를 경우 주문 실패")
    @Test
    public void create_invalid_price() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(makeExistAndNotEqualPriceMenuOrderLineItems());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("주문이 배송이고 배송지 정보가 없을 경우 주문 실패")
    @Test
    public void create_order_delivery_and_no_delivery_address() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(makeExistOrderLineItems());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
    }

    @DisplayName("주문이 먹고가기이고 유효한 테이블이 아닐 경우 주문 실패")
    @Test
    public void create_order_eat_in_and_non_exist_order_table() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(makeExistOrderLineItems());
        order.setOrderTableId(new UUID(1, 2));

        //when & then
        assertThrows(NoSuchElementException.class, () -> orderService.create(order));
    }

    @DisplayName("주문이 먹고가기이고 테이블이 점유되어있을 경우 주문 실패")
    @Test
    public void create_order_eat_in_and_order_table_occupied() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(makeExistOrderLineItems());
        order.setOrderTableId(makeOccupiedOrderTable().getId());

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.create(order));
    }

    @DisplayName("주문의 먹고가기이고 테이블이 비어있을 경우 주문 성공")
    @Test
    public void create_order_eat_in_and_order_table_not_occupied() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(makeExistOrderLineItems());
        order.setOrderTableId(makeNotOccupiedOrderTable().getId());

        //when & then
        assertThat(orderService.create(order))
                .isNotNull();
    }

    @DisplayName("주문이 존재하지 않을 경우 주문 수락 실패")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderId")
    @ParameterizedTest
    public void accept_non_exist_order(UUID orderId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderService.accept(orderId));
    }

    @DisplayName("주문이 대기상태가 아닐 경우 수락 실패")
    @Test
    public void accept_order_not_waiting() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));
    }

    @DisplayName("주문이 대기상태일 경우 수락")
    @Test
    public void accept_order_waiting() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        //when & then
        assertThat(orderService.accept(order.getId()))
                .isNotNull();
    }

    @DisplayName("주문이 존재하지 않을 경우 주문 서빙 실패")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderId")
    @ParameterizedTest
    public void serve_non_exist_order(UUID orderId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderService.serve(orderId));
    }

    @DisplayName("주문이 수락상태가 아닐 경우 서빙 실패")
    @Test
    public void serve_order_not_accepted() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.serve(order.getId()));
    }

    @DisplayName("주문이 수락상태일 경우 서빙")
    @Test
    public void serve_order_accepted() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        //when & then
        assertThat(orderService.serve(order.getId()))
                .isNotNull();
    }

    @DisplayName("주문이 존재하지 않을 경우 주문 배송 시작 실패")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderId")
    @ParameterizedTest
    public void startDelivery_non_exist_order(UUID orderId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderService.startDelivery(orderId));
    }

    @DisplayName("주문이 배송이 아닐 경우 주문 배송 시작 실패")
    @Test
    public void startDelivery_order_not_delivery() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        orderRepository.save(order);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("주문이 서빙되지 않았을 경우 주문 배송 시작 실패")
    @Test
    public void startDelivery_order_not_served() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("주문이 서빙되었을 경우 주문 배송 시작")
    @Test
    public void startDelivery_order_served() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);

        //when & then
        assertThat(orderService.startDelivery(order.getId()))
                .isNotNull();
    }

    @DisplayName("주문이 존재하지 않을 경우 주문 배송 완료 실패")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderId")
    @ParameterizedTest
    public void completeDelivery_non_exist_order(UUID orderId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderService.completeDelivery(orderId));
    }

    @DisplayName("주문이 배송이 아닐 경우 주문 배송 완료 실패")
    @Test
    public void completeDelivery_order_not_delivery() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.EAT_IN);
        orderRepository.save(order);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("주문이 배송일 경우 주문 배송 완료")
    @Test
    public void completeDelivery_order_delivery() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

        //when & then
        assertThat(orderService.completeDelivery(order.getId()))
                .isNotNull();
    }

    @DisplayName("주문이 존재하지 않을 경우 주문 완료 실패")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderId")
    @ParameterizedTest
    public void complete_non_exist_order(UUID orderId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderService.complete(orderId));
    }

    @DisplayName("주문이 배송이고, 배송이 아직 안됐을 경우 주문 완료 실패")
    @Test
    public void complete_delivery_not_delivered() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
    }

    @DisplayName("주문이 배송이고, 배송이 됐을 경우 주문 완료 성공")
    @Test
    public void complete_eat_in_and_not_served() {
        //given
        Order order = makeRandomOrder();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        //when & then
        assertThat(orderService.complete(order.getId()))
                .isNotNull();
    }

    private Order makeRandomOrder() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }

    private List<OrderLineItem> makeNonExistOrderLineItems() {
        List<OrderLineItem> orderLineItemList = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(new UUID(1, 2));
        orderLineItemList.add(orderLineItem);
        return orderLineItemList;
    }

    private List<OrderLineItem> makeRandomOrderLineItems() {
        List<OrderLineItem> orderLineItemList = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItemList.add(orderLineItem);
        return orderLineItemList;
    }

    private List<OrderLineItem> makeExistOrderLineItems() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("test");
        menuGroupRepository.save(menuGroup);

        List<Menu> menuList = new ArrayList<>();
        List<OrderLineItem> orderLineItemList = makeRandomOrderLineItems();
        for (OrderLineItem orderLineItem : orderLineItemList) {
            Menu menu = new Menu();
            menu.setId(orderLineItem.getMenuId());
            menu.setName("asdf");
            menu.setMenuGroupId(menuGroup.getId());
            menu.setMenuGroup(menuGroup);
            menu.setPrice(BigDecimal.valueOf(1000));
            menu.setDisplayed(true);
            menuList.add(menu);
            orderLineItem.setPrice(menu.getPrice());
            orderLineItem.setMenu(menu);
        }
        menuRepository.saveAll(menuList);
        return orderLineItemList;
    }

    private List<OrderLineItem> makeExistAndInvalidQuantityOrderLineItems() {
        List<OrderLineItem> orderLineItems = makeExistOrderLineItems();
        for (OrderLineItem orderLineItem : orderLineItems) {
            orderLineItem.setQuantity(-1);
        }
        return orderLineItems;
    }

    private List<OrderLineItem> makeExistAndNonDisplayMenuOrderLineItems() {
        List<OrderLineItem> orderLineItems = makeExistOrderLineItems();
        for (OrderLineItem orderLineItem : orderLineItems) {
            orderLineItem.getMenu().setDisplayed(false);
            menuRepository.save(orderLineItem.getMenu());
        }
        return orderLineItems;
    }

    private List<OrderLineItem> makeExistAndNotEqualPriceMenuOrderLineItems() {
        List<OrderLineItem> orderLineItems = makeExistOrderLineItems();
        for (OrderLineItem orderLineItem : orderLineItems) {
            orderLineItem.setPrice(orderLineItem.getMenu().getPrice().add(BigDecimal.ONE));
        }
        return orderLineItems;
    }

    private OrderTable makeOccupiedOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("test");
        orderTable.setOccupied(true);
        return orderTableRepository.save(orderTable);
    }

    private OrderTable makeNotOccupiedOrderTable() {
        OrderTable orderTable = makeOccupiedOrderTable();
        orderTable.setOccupied(false);
        return orderTableRepository.save(orderTable);
    }
}