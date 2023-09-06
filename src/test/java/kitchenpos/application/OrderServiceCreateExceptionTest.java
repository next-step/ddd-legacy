package kitchenpos.application;

import static kitchenpos.application.constant.KitchenposTestConst.TEST_DELIVERY_ADDRESS;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_DATE_TIME;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_LINE_PRICE;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_TABLE_NAME;
import static kitchenpos.application.constant.KitchenposTestConst.TEST_ORDER_TABLE_NUMBER_OF_GUEST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.application.fakerepository.MenuFakeRepository;
import kitchenpos.application.fakerepository.OrderFakeRepository;
import kitchenpos.application.fakerepository.OrderTableFakeRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Nested
@DisplayName("OrderService의 create메소드 호출시 예외 발생 조건 테스트")
public class OrderServiceCreateExceptionTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient mockRidersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderFakeRepository();
        menuRepository = new MenuFakeRepository();
        orderTableRepository = new OrderTableFakeRepository();

        sut = new OrderService(orderRepository, menuRepository,
            orderTableRepository, mockRidersClient);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 주문_생성_요청의_인자인 {

        @ParameterizedTest
        @NullSource
        void 주문타입이_null이면_예외를_발생시킨다(final OrderType type) {

            assertThatThrownBy(() -> sut.create(create(type)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 주문상품들이_null이거나_비어있으면_예외를_발생시킨다(final List<OrderLineItem> values) {
            assertThatThrownBy(() -> sut.create(createTargetOrder(values)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문상품들에_포함된_menu의_수와_주문상품들의_수가_다르면_예외를_발생시킨다__menu가_없는_menu면_예외를_발생시킨다() {
            // given
            final Menu menu1 = menuRepository.save(new Menu());
            final Menu menu2 = new Menu();
            menu2.setId(UUID.randomUUID());
            final OrderLineItem orderLineItem1 = create(menu1);
            final OrderLineItem orderLineItem2 = create(menu2);

            // when
            final Order order = createTargetOrder(List.of(orderLineItem1, orderLineItem2));

            // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }


        @Test
        void 각_주문상품들의_menu가_하나라도_숨겨져_있으면_예외를_발생시킨다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);

            final Menu displayedMenu = menuRepository.save(create(true, price));
            final Menu hidedMenu = menuRepository.save(create(false, price));
            final OrderLineItem orderLineItem1 = create(displayedMenu, price);
            final OrderLineItem orderLineItem2 = create(hidedMenu, price);

            // when
            final Order order = createTargetOrder(List.of(orderLineItem1, orderLineItem2));

            // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        void 각_주문상품들의_menu의_가격이_주문상품들의_가격과_하나라도_다르면_예외를_발생시킨다() {
            // given
            final BigDecimal PRICE = BigDecimal.valueOf(1_000);
            final BigDecimal DIFERRENT_PRICE = BigDecimal.valueOf(2_000);

            final Menu menu = menuRepository.save(create(PRICE));
            final OrderLineItem orderLineItem1 = create(menu, DIFERRENT_PRICE);

            // when
            final Order order = createTargetOrder(List.of(orderLineItem1));

            // then
            assertThatThrownBy(() -> sut.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        private Order createTargetOrder(final List<OrderLineItem> orderLineItems) {
            return createOrderRequest(OrderType.EAT_IN, OrderStatus.SERVED, TEST_ORDER_DATE_TIME,
                orderLineItems, TEST_DELIVERY_ADDRESS, createOccupiedOrderTable());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 배달_주문일_때 {

        @ParameterizedTest
        @NullAndEmptySource
        void 배달주소가_null이거나_비어있으면_예외를_발생시킨다(final String value) {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);
            final Menu menu = menuRepository.save(create(price));
            final OrderLineItem orderLineItem1 = create(menu, price);

            assertThatThrownBy(() -> sut.create(createTargetOrder(List.of(orderLineItem1), value)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문상품들의_각_수량이_음수면_예외를_발생시킨다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);
            final Menu menu = menuRepository.save(create(price));
            final OrderLineItem orderLineItem1 = create(menu, -1);

            assertThatThrownBy(() -> sut.create(createTargetOrder(List.of(orderLineItem1))))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        private Order createTargetOrder(final List<OrderLineItem> orderLineItems,
            final String deliveryAddress) {

            return createOrderRequest(OrderType.DELIVERY, OrderStatus.SERVED, TEST_ORDER_DATE_TIME,
                orderLineItems, deliveryAddress, createOccupiedOrderTable());
        }

        private Order createTargetOrder(final List<OrderLineItem> orderLineItems) {
            return createTargetOrder(orderLineItems, TEST_DELIVERY_ADDRESS);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 매장_주문일_때 {

        @Test
        void 주문상품들의_각_수량이_음수면_예외를_발생시킨다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);
            final Menu menu = menuRepository.save(create(price));
            final OrderLineItem orderLineItem1 = create(menu, -1);

            assertThatThrownBy(() -> sut.create(createTargetOrder(List.of(orderLineItem1))))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void order에_할당된_orderTable이_없으면_예외를_발생시킨다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);
            final Menu menu = menuRepository.save(create(price));
            final OrderLineItem orderLineItem1 = create(menu, price);

            // when & then
            assertThatThrownBy(() -> sut.create(createTargetOrder(List.of(orderLineItem1))))
                .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        void order에_할당된_orderTable이_비어있으면_예외를_발생시킨다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);

            final OrderTable orderTable = orderTableRepository.save(createUnoccupiedOrderTable());
            final Menu menu = menuRepository.save(create(price));
            final OrderLineItem orderLineItem1 = create(menu, price);

            // when & then
            assertThatThrownBy(
                () -> sut.create(createTargetOrder(List.of(orderLineItem1), orderTable)))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        private Order createTargetOrder(final List<OrderLineItem> orderLineItems) {
            return createTargetOrder(orderLineItems, createOccupiedOrderTable());
        }


        private Order createTargetOrder(final List<OrderLineItem> orderLineItems,
            final OrderTable orderTable) {

            return createOrderRequest(OrderType.EAT_IN, OrderStatus.SERVED, TEST_ORDER_DATE_TIME,
                orderLineItems, null, orderTable);
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 포장_주문일_때 {

        @Test
        void 주문상품들의_각_수량이_음수면_예외를_발생시킨다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(1_000);
            final Menu menu = menuRepository.save(create(price));
            final OrderLineItem orderLineItem1 = create(menu, -1);

            assertThatThrownBy(() -> sut.create(createTargetOrder(List.of(orderLineItem1))))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        private Order createTargetOrder(final List<OrderLineItem> orderLineItems) {
            return createOrderRequest(OrderType.TAKEOUT, OrderStatus.SERVED, TEST_ORDER_DATE_TIME,
                orderLineItems, null, createOccupiedOrderTable());
        }
    }


    private OrderLineItem create(final Menu menu) {
        return create(menu, TEST_ORDER_LINE_PRICE);
    }


    private OrderLineItem create(final Menu menu, final int quantity) {
        return create(menu, TEST_ORDER_LINE_PRICE, quantity);
    }

    private OrderLineItem create(final Menu menu, final BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);

        return create(menu, price, 2);
    }

    private OrderLineItem create(final Menu menu, final BigDecimal price, final int quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

    private Order create(final OrderType type) {
        final Order order = new Order();
        order.setType(type);

        return order;
    }

    private Menu create(final BigDecimal price) {
        return create(true, price);
    }

    private Menu create(final boolean displayed, final BigDecimal price) {
        final Menu menu = new Menu();
        menu.setDisplayed(displayed);
        menu.setPrice(price);

        return menu;
    }

    private OrderTable createOccupiedOrderTable() {
        return createOrderTableRequest(TEST_ORDER_TABLE_NAME,
            TEST_ORDER_TABLE_NUMBER_OF_GUEST, true);
    }

    private OrderTable createUnoccupiedOrderTable() {
        return createOrderTableRequest(TEST_ORDER_TABLE_NAME,
            TEST_ORDER_TABLE_NUMBER_OF_GUEST, false);
    }

    private OrderTable createOrderTableRequest(final String name, final int numberOfGuests,
        final boolean occupied) {

        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);

        return orderTable;
    }
    
    private Order createOrderRequest(final OrderType type, final OrderStatus status,
        final LocalDateTime orderDateTime, final List<OrderLineItem> orderLineItems,
        final String deliveryAddress, final OrderTable orderTable) {

        final Order order = new Order();
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());

        return order;
    }
}
