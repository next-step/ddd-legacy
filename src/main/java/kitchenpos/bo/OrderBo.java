package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderBo {
    private final MenuDao menuDao;
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final OrderTableDao orderTableDao;

    public OrderBo(
            final MenuDao menuDao,
            final OrderDao orderDao,
            final OrderLineItemDao orderLineItemDao,
            final OrderTableDao orderTableDao
    ) {
        this.menuDao = menuDao;
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.orderTableDao = orderTableDao;
    }

    /**
     * 주문 생성
     *
     * @param order
     * @return
     */
    @Transactional
    public Order create(final Order order) {
        final List<OrderLineItem> orderLineItems = order.getOrderLineItems(); // 주문에 포함될 메뉴 리스트

        if (CollectionUtils.isEmpty(orderLineItems)) { // 주문 시 1개 이상의 메뉴를 시켜야한다.
            throw new IllegalArgumentException();
        }

        final List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());

        if (orderLineItems.size() != menuDao.countByIdIn(menuIds)) { // 주문 내에 동일 메뉴가 들어갔는지 체크
            throw new IllegalArgumentException();
        }

        order.setId(null);

        final OrderTable orderTable = orderTableDao.findById(order.getOrderTableId()) // 테이블 조회
                .orElseThrow(IllegalArgumentException::new);

        if (orderTable.isEmpty()) { // 테이블이 공석이면 에러.
            throw new IllegalArgumentException();
        }

        order.setOrderTableId(orderTable.getId()); // 테이블 세팅
        order.setOrderStatus(OrderStatus.COOKING.name()); // 주문 생성 시, 주문상태는 조리중이다.
        order.setOrderedTime(LocalDateTime.now());

        final Order savedOrder = orderDao.save(order); // 주문 저장

        final Long orderId = savedOrder.getId();
        final List<OrderLineItem> savedOrderLineItems = new ArrayList<>();
        for (final OrderLineItem orderLineItem : orderLineItems) {
            orderLineItem.setOrderId(orderId);
            savedOrderLineItems.add(orderLineItemDao.save(orderLineItem)); // 주문메뉴 저장
        }
        savedOrder.setOrderLineItems(savedOrderLineItems);

        return savedOrder;
    }

    /**
     * 전체 주문 리스트 조회
     *
     * @return
     */
    public List<Order> list() {
        final List<Order> orders = orderDao.findAll();

        for (final Order order : orders) {
            order.setOrderLineItems(orderLineItemDao.findAllByOrderId(order.getId())); // 주문메뉴 리스트 조회
        }

        return orders;
    }

    /**
     * 주문 상태 변경
     *
     * @param orderId
     * @param order
     * @return
     */
    @Transactional
    public Order changeOrderStatus(final Long orderId, final Order order) {
        final Order savedOrder = orderDao.findById(orderId) // 주문 조회
                .orElseThrow(IllegalArgumentException::new);

        if (Objects.equals(OrderStatus.COMPLETION.name(), savedOrder.getOrderStatus())) { // 이미 완료 상태의 주문인지 체크
            throw new IllegalArgumentException();
        }

        final OrderStatus orderStatus = OrderStatus.valueOf(order.getOrderStatus());
        savedOrder.setOrderStatus(orderStatus.name()); // 새로운 주문상태 세팅

        orderDao.save(savedOrder); // 주문 수정

        savedOrder.setOrderLineItems(orderLineItemDao.findAllByOrderId(orderId));

        return savedOrder;
    }
}
