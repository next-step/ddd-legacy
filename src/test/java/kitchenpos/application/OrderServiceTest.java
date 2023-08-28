package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.objectmother.*;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    @Autowired
    private OrderService orderService;

    private MenuGroup 메뉴그룹;
    private Product 상품_1;
    private Product 상품_2;
    private MenuProduct 메뉴상품_1;
    private MenuProduct 메뉴상품_2;
    private OrderTable 착석테이블;
    private OrderTable 미착석테이블;
    private Menu 메뉴_1;
    private Menu 메뉴_2;
    private Menu 비노출메뉴;

    @BeforeEach
    void setUp() {
        메뉴그룹 = menuGroupRepository.save(MenuGroupMaker.make("메뉴그룹"));
        상품_1 = productRepository.save(ProductMaker.make("상품1", 1500L));
        상품_2 = productRepository.save(ProductMaker.make("상품2", 3000L));
        메뉴상품_1 = MenuProductMaker.make(상품_1, 2);
        메뉴상품_2 = MenuProductMaker.make(상품_2, 5);
        착석테이블 = orderTableRepository.save(OrderTableMaker.make("착석테이블", 4));
        미착석테이블 = orderTableRepository.save(OrderTableMaker.make("미착석테이블"));
        메뉴_1 = menuRepository.save(MenuMaker.make("메뉴1", 15_000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
        메뉴_2 = menuRepository.save(MenuMaker.make("메뉴2", 12_000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
        비노출메뉴 = menuRepository.save(MenuMaker.makeHideMenu("비노출메뉴", 12_000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
    }

    @DisplayName("매장주문생성시 요청한 데이터로 주문이 생성되야 한다.")
    @Test
    void 매장주문생성() {
        // given
        Order order = OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15_000L));

        // when
        Order saveOrder = orderService.create(order);

        // then
        assertThat(saveOrder.getType()).isEqualTo(EAT_IN);
        assertThat(saveOrder.getStatus()).isEqualTo(WAITING);
        assertThat(saveOrder.getOrderDateTime()).isNotNull();
        assertThat(saveOrder.getOrderLineItems())
                .hasSize(1)
                .extracting(OrderLineItem::getMenu)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
        assertThat(saveOrder.getOrderTable())
                .extracting(OrderTable::getName, OrderTable::getNumberOfGuests, OrderTable::isOccupied)
                .containsExactly(착석테이블.getName(), 4, true);
    }

    @DisplayName("매장주문생성시 테이블에 착석한 손님이 아닐경우 에러를 던진다.")
    @Test
    void 매장주문생성실패_미착석() {
        // given
        Order order = OrderMaker.makeEatin(미착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15_000L));

        // when then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문생성시 비노출메뉴를 주문할경우 에러를 던진다.")
    @Test
    void 주문생성실패_비노출메뉴() {
        // given
        Order order = OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(비노출메뉴, 1, 15_000L));

        // when then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문생성시 주문가격이 메뉴가격과 일치하지 않을경우 에러를 던진다.")
    @Test
    void 주문생성실패_주문가격_메뉴가격_불일치() {
        // given
        Order order = OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 10_000L));

        // when then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문생성시 메뉴수량이 음수일경우 에러를 던진다.")
    @Test
    void 주문생성실패_수량음수() {
        // given
        Order order = OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, -1, 15_000L));

        // when then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문생성시 요청한 데이터로 주문이 생성되야 한다.")
    @Test
    void 배달주문생성() {
        // given
        Order order = OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15_000L));

        // when
        Order saveOrder = orderService.create(order);

        // then
        assertThat(saveOrder.getType()).isEqualTo(DELIVERY);
        assertThat(saveOrder.getStatus()).isEqualTo(WAITING);
        assertThat(saveOrder.getOrderDateTime()).isNotNull();
        assertThat(saveOrder.getDeliveryAddress()).isEqualTo("넥스트타워");
        assertThat(saveOrder.getOrderLineItems())
                .hasSize(1)
                .extracting(OrderLineItem::getMenu)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    @DisplayName("포장주문생성시 요청한 데이터로 주문이 생성되야 한다.")
    @Test
    void 포장주문생성() {
        // given
        Order order = OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15_000L));

        // when
        Order saveOrder = orderService.create(order);

        // then
        assertThat(saveOrder.getType()).isEqualTo(TAKEOUT);
        assertThat(saveOrder.getStatus()).isEqualTo(WAITING);
        assertThat(saveOrder.getOrderDateTime()).isNotNull();
        assertThat(saveOrder.getOrderLineItems())
                .hasSize(1)
                .extracting(OrderLineItem::getMenu)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    @DisplayName("주문대기중인 주문을 수락할경우 해당주문이 수락된다.")
    @Test
    void 주문수락() {
        // given
        Order order = orderService.create(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15_000L)));

        // when
        Order acceptOrder = orderService.accept(order.getId());

        // then
        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문대기중이 아닌 주문을 수락할경우 에러를 던진다..")
    @Test
    void 주문수락실패_대기상태아닐경우() {
        // given
        Order order = orderService.create(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15_000L)));
        orderService.accept(order.getId());

        // when then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문대기중인 배달주문을 수락할경우 주문이 수락되며 배달요청을 수행한다.")
    @Test
    void 배달주문수락() {
        // given
        Order order = orderService.create(
                OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );

        // when
        orderService.accept(order.getId());

        // then
        verify(kitchenridersClient, times(1)).requestDelivery(any(), any(), any());
    }

    @DisplayName("주문상태가 수락일경우 제공이 가능하다.")
    @Test
    void 주문제공() {
        // given
        Order order = orderService.create(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15_000L)));
        orderService.accept(order.getId());

        // when
        Order serveOrder = orderService.serve(order.getId());

        // then
        assertThat(serveOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달주문인경우 주문이 제공된경우 배달을 시작할 수 있다.")
    @Test
    void 배달주문_배달시작() {
        // given
        Order order = orderService.create(
                OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when
        Order deliveryOrder = orderService.startDelivery(order.getId());

        // then
        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달주문인경우 주문이 제공된경우 배달을 시작할 수 있다.")
    @Test
    void 배달주문_배달완료() {
        // given
        Order order = orderService.create(
                OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());

        // when
        Order completeDeliveryOrder = orderService.completeDelivery(order.getId());

        // then
        assertThat(completeDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("매장주문인 경우 주문상태가 제공인 경우 완료할 수 있으며 테이블을 치운다.")
    @Test
    void 매장주문완료() {
        // given
        Order order = orderService.create(OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15_000L)));
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when
        Order completeOrder = orderService.complete(order.getId());

        // then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(completeOrder.getOrderTable())
                .extracting(OrderTable::getNumberOfGuests, OrderTable::isOccupied)
                .containsExactly(0, false);
    }

    @DisplayName("배달주문인 경우 배달이 완료된 경우 완료할 수 있다.")
    @Test
    void 배달주문완료() {
        // given
        Order order = orderService.create(
                OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());
        orderService.completeDelivery(order.getId());

        // when
        Order completeOrder = orderService.complete(order.getId());

        // then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장주문인 경우 제공되었으면 완료할 수 있다.")
    @Test
    void 포장주문완료() {
        // given
        Order order = orderService.create(OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15_000L)));
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when
        Order completeOrder = orderService.complete(order.getId());

        // then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문을 전체조회 할 수 있다.")
    @Test
    void 주문전체조회() {
        // given
        Order eatinOrder = orderService.create(
                OrderMaker.makeEatin(착석테이블, OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );
        Order deliveryOrder = orderService.create(
                OrderMaker.makeDelivery("넥스트타워", OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );
        Order takeoutOrder = orderService.create(
                OrderMaker.makeTakeout(OrderLineItemMaker.make(메뉴_1, 1, 15_000L))
        );

        orderService.accept(takeoutOrder.getId());
        orderService.serve(takeoutOrder.getId());

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders)
                .hasSize(3)
                .extracting(Order::getType, Order::getStatus)
                .containsExactly(
                        Tuple.tuple(EAT_IN, WAITING),
                        Tuple.tuple(DELIVERY, WAITING),
                        Tuple.tuple(TAKEOUT, SERVED)
                );
    }
}