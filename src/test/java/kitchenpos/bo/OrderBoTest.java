package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @Mock
    private MenuDao menuDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderLineItemDao orderLineItemDao;
    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    OrderBo orderBo;

//    private MenuGroup mockMenuGroup;
//    private MenuProduct mockMenuProduct;
//    private Product mockProduct;
    private OrderLineItem mockOrderLineItem;
    private OrderTable mockOrderTable;
//    private TableGroup mockTableGroup;

    @BeforeEach
    void beforeEach() {
//        mockMenuGroup = new MenuGroup();
//        mockMenuGroup.setId(1L);
//        mockMenuGroup.setName("메뉴그룹1");

//        mockMenuProduct = new MenuProduct();
//        mockMenuProduct.setSeq(1L);
//        mockMenuProduct.setMenuId(1L);
//        mockMenuProduct.setProductId(1L);
//        mockMenuProduct.setQuantity(1);

//        mockProduct = new Product();
//        mockProduct.setId(1L);
//        mockProduct.setName("저세상치킨");
//        mockProduct.setPrice(BigDecimal.valueOf(1000));

        mockOrderLineItem = new OrderLineItem();
        mockOrderLineItem.setSeq(1L);
        mockOrderLineItem.setOrderId(1L);
        mockOrderLineItem.setMenuId(1L);
        mockOrderLineItem.setQuantity(2);

        mockOrderTable = new OrderTable();
        mockOrderTable.setId(1L);
        mockOrderTable.setTableGroupId(1L);
        mockOrderTable.setNumberOfGuests(5);
        mockOrderTable.setEmpty(false);

//        mockTableGroup = new TableGroup();
//        mockTableGroup.setId(1L);
//        mockTableGroup.setOrderTables(new ArrayList<>(Arrays.asList(mockOrderTable)));
//        mockTableGroup.setCreatedDate(LocalDateTime.now());

    }

    @DisplayName("새로운 주문을 생성할 수 있다.")
    @Test
    void create() {
        // given
        Order newOrder = new Order();
        // 주문은 테이블에서 받을 수 있다.
        // 테이블에서 다수의 주문을 받을 수 있다.
        newOrder.setOrderTableId(mockOrderTable.getId());
        newOrder.setOrderLineItems(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));
        given(orderDao.save(newOrder)).willAnswer(invocation -> {
            newOrder.setId(1L);
            return newOrder;
        });
        given(orderLineItemDao.save(any(OrderLineItem.class))).willReturn(mockOrderLineItem);

        // when
        final Order result = orderBo.create(newOrder);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderTableId()).isEqualTo(mockOrderTable.getId());
        // 주문 시, 주문상태는 조리중이다.
        // 주문상태는 조리중/식사중/완료가 있다.
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.getOrderedTime()).isNotNull();
        assertThat(result.getOrderLineItems().size()).isEqualTo(1);
        assertThat(result.getOrderLineItems().get(0).getSeq()).isEqualTo(mockOrderLineItem.getSeq());
    }

    @DisplayName("새로운 주문 생성 시, 테이블이 공석이면 안된다.")
    @Test
    void tableShouldNotBeEmpty() {
        // given
        mockOrderTable.setEmpty(true);

        Order newOrder = new Order();
        newOrder.setOrderTableId(mockOrderTable.getId());
        newOrder.setOrderLineItems(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(mockOrderTable.getId())).willReturn(Optional.of(mockOrderTable));

        // when
        // then
        assertThatThrownBy(() -> {
            orderBo.create(newOrder);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 시 1개 이상의 메뉴를 시킬 수 있으며, 메뉴별로 1개 이상 시킬 수 있다.")
    @Test
    void orderShouldContainAtLeastOneMenu() {
        // given
        Order newOrder = new Order();
        newOrder.setOrderTableId(mockOrderTable.getId());
        newOrder.setOrderLineItems(new ArrayList<>());

        // when
        // then
        assertThatThrownBy(() -> {
            orderBo.create(newOrder);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("전체 주문 리스트를 조회할 수 있으며, 주문메뉴 리스트도 같이 조회된다.")
    @Test
    void list() {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(mockOrderTable.getId());
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        given(orderDao.findAll()).willReturn(new ArrayList<>(Arrays.asList(order)));
        given(orderLineItemDao.findAllByOrderId(order.getId())).willReturn(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        // when
        final List<Order> result = orderBo.list();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(order.getId());
        assertThat(result.get(0).getOrderTableId()).isEqualTo(order.getOrderTableId());
        assertThat(result.get(0).getOrderStatus()).isEqualTo(order.getOrderStatus());
        assertThat(result.get(0).getOrderedTime()).isEqualTo(order.getOrderedTime());
        assertThat(result.get(0).getOrderLineItems().size()).isEqualTo(order.getOrderLineItems().size());
        assertThat(result.get(0).getOrderLineItems().get(0).getSeq()).isEqualTo(order.getOrderLineItems().get(0).getSeq());
    }

    @DisplayName("주문의 주문상태를 변경할 수 있다.")
    @ParameterizedTest
    @MethodSource(value = "provideOrderStatus")
    void changeOrderStatus(OrderStatus newOrderStatus) {
        // given
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setOrderTableId(mockOrderTable.getId());
        savedOrder.setOrderStatus(OrderStatus.COOKING.name());
        savedOrder.setOrderedTime(LocalDateTime.now());
        savedOrder.setOrderLineItems(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        Order newOrder = new Order();
        newOrder.setOrderStatus(newOrderStatus.name());

        given(orderDao.findById(savedOrder.getId())).willReturn(Optional.of(savedOrder));
        given(orderLineItemDao.findAllByOrderId(savedOrder.getId())).willReturn(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        // when
        final Order result = orderBo.changeOrderStatus(savedOrder.getId(), newOrder);

        // then
        assertThat(result.getId()).isEqualTo(savedOrder.getId());
        assertThat(result.getOrderTableId()).isEqualTo(savedOrder.getOrderTableId());
        assertThat(result.getOrderStatus()).isEqualTo(newOrderStatus.name());
        assertThat(result.getOrderedTime()).isEqualTo(savedOrder.getOrderedTime());
        assertThat(result.getOrderLineItems().size()).isEqualTo(savedOrder.getOrderLineItems().size());
        assertThat(result.getOrderLineItems().get(0).getSeq()).isEqualTo(savedOrder.getOrderLineItems().get(0).getSeq());
    }

    private static Stream<OrderStatus> provideOrderStatus() {
        return Stream.of(OrderStatus.COOKING, OrderStatus.MEAL);
    }

    @DisplayName("주문의 주문상태 변경 시, 이미 주문상태가 완료인 경우에는 변경할 수 없다.")
    @Test
    void changeOrderStatusThrowErrorWhenCompletion() {
        // given
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setOrderTableId(mockOrderTable.getId());
        savedOrder.setOrderStatus(OrderStatus.COMPLETION.name());
        savedOrder.setOrderedTime(LocalDateTime.now());
        savedOrder.setOrderLineItems(new ArrayList<>(Arrays.asList(mockOrderLineItem)));

        Order newOrder = new Order();
        newOrder.setOrderStatus(OrderStatus.COOKING.name());

        given(orderDao.findById(savedOrder.getId())).willReturn(Optional.of(savedOrder));

        // when
        // then
        assertThatThrownBy(() -> {
            orderBo.changeOrderStatus(savedOrder.getId(), newOrder);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
