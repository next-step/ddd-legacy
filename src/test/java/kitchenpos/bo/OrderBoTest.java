package kitchenpos.bo;

import kitchenpos.dao.TestOrderLineItemDao;
import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderBoTest {

    private MenuDao menuDao = new TestMenuDao();
    private OrderDao orderDao = new TestOrderDao();
    private OrderLineItemDao orderLineItemDao = new TestOrderLineItemDao();
    private OrderTableDao orderTableDao = new TestOrderTableDao();

    private OrderBo orderBo;

    private Random random = new Random();

    @BeforeEach
    void setUp() {
        orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);

        final Menu friedAndSeasoned = new Menu() {{
            setId(1L);
            setName("후라이드 반 양념 반");
        }};
        menuDao.save(friedAndSeasoned);

        final Menu soyAndSeasoned = new Menu() {{
            setId(2L);
            setName("간장 반 양념 반");
        }};
        menuDao.save(soyAndSeasoned);

        final OrderTable orderTable = new OrderTable() {{
            setId(1L);
            setEmpty(false);
            setNumberOfGuests(3);
        }};
        orderTableDao.save(orderTable);
    }

    @DisplayName("주문을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final Order expected = createOrder();

        // when
        final Order actual = orderBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getOrderTableId()).isEqualTo(expected.getOrderTableId());
        assertThat(actual.getOrderStatus()).isEqualTo(expected.getOrderStatus());
        assertThat(actual.getOrderLineItems().size()).isEqualTo(expected.getOrderLineItems().size());
    }

    @DisplayName("주문한 메뉴가 없는 경우 등록할 수 없다.")
    @Test
    void emptyOrderLineItem() {
        // given
        final Order expected = createOrderWithoutOrderLineItem();

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(expected));
    }

    @DisplayName("등록되지 않은 메뉴는 주문할 수 없다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "3"})
    void unregisteredOrderLineItem(final Long menuId) {
        // given
        final Order expected = createOrderWithoutOrderLineItem();
        expected.setOrderLineItems(Arrays.asList(
                new OrderLineItem() {{
                    setSeq(1L);
                    setOrderId(expected.getId());
                    setMenuId(menuId);
                    setQuantity(1L);
                }}
        ));

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(expected));
    }

    @DisplayName("각 메뉴는 1개 이상 주문할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1000", "-1", "0"})
    void invalidOrderLineItemQuantity(final long quantity) {
        // given
        final Order expected = createOrderWithoutOrderLineItem();
        expected.setOrderLineItems(Arrays.asList(
                new OrderLineItem() {{
                    setSeq(1L);
                    setOrderId(expected.getId());
                    setMenuId(3L);
                    setQuantity(quantity);
                }}
        ));

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(expected));
    }

    @DisplayName("테이블이 비어있는 경우 주문을 받을 수 없다.")
    @Test
    void emptyTable() {
        // given
        final OrderTable emptyTable = new OrderTable() {{
            setId(100L);
            setEmpty(false);
            setNumberOfGuests(3);
        }};
        orderTableDao.save(emptyTable);

        final Order expected = createOrderWithOrderTable(emptyTable.getTableGroupId());

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(expected));
    }

    @DisplayName("주문의 최초 상태는 조리 중이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"MEAL", "COMPLETION"})
    void invalidOrderStatus(OrderStatus orderStatus) {
        // given
        final Order expected = createOrder(random.nextLong(), orderStatus.name());

        // when
        final Order actual = orderBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("주문 목록을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final Order expected = createOrder();
        orderDao.save(expected);

        // when
        final List<Order> actual = orderBo.list();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).contains(expected);
    }

    @DisplayName("주문 상태를 변경할 수 있다.")
    @Test
    void changeOrderStatus() {
        // given
        final Long orderId = 1L;
        orderDao.save(createOrder(orderId, OrderStatus.COOKING.name()));

        final Order expected = createOrder(orderId, OrderStatus.MEAL.name());

        // when
        final Order actual = orderBo.changeOrderStatus(orderId, expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getOrderStatus()).isEqualTo(expected.getOrderStatus());
    }

    @DisplayName("이미 식사 완료된 주문은 상태를 변경할 수 없다.")
    @Test
    void cannotChangeOrderStatus() {
        // given
        final Long orderId = 1L;
        orderDao.save(createOrder(orderId, OrderStatus.COMPLETION.name()));

        final Order expected = createOrder(orderId, OrderStatus.MEAL.name());

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(orderId, expected));
    }

    private Order createOrder() {
        return createOrder(random.nextLong(), OrderStatus.COOKING.name());
    }

    private Order createOrder(Long id, String orderStatus) {
        return createOrder(id, orderStatus, 1L);
    }

    private Order createOrder(Long id, String orderStatus, Long orderTableId) {
        Order order = new Order();
        order.setId(id);
        order.setOrderTableId(orderTableId);
        order.setOrderStatus(orderStatus);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(Arrays.asList(
                new OrderLineItem() {{
                    setSeq(1L);
                    setOrderId(id);
                    setMenuId(1L);
                    setQuantity(1L);
                }},
                new OrderLineItem() {{
                    setSeq(2L);
                    setOrderId(id);
                    setMenuId(2L);
                    setQuantity(1L);
                }}
        ));

        return order;
    }

    private Order createOrderWithOrderTable(Long orderTableId) {
        return createOrder(random.nextLong(), OrderStatus.COOKING.name(), orderTableId);
    }

    private Order createOrderWithoutOrderLineItem() {
        Order order = new Order();
        order.setId(random.nextLong());
        order.setOrderTableId(1L);
        order.setOrderStatus(OrderStatus.COOKING.name());
        order.setOrderedTime(LocalDateTime.now());

        return order;
    }
}
