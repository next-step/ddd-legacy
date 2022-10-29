package kitchenpos.order.domain;

import kitchenpos.order.vo.DeliveryAddress;
import kitchenpos.ordertable.domain.OrderTable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(name = "orders")
@Entity
public class Order {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "order_id",
            nullable = false,
            columnDefinition = "binary(16)",
            foreignKey = @ForeignKey(name = "fk_order_line_item_to_orders")
    )
    private List<OrderLineItem> orderLineItems;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @ManyToOne
    @JoinColumn(
            name = "order_table_id",
            columnDefinition = "binary(16)",
            foreignKey = @ForeignKey(name = "fk_orders_to_order_table")
    )
    private OrderTable orderTable;

    @Transient
    private UUID orderTableId;

    protected Order() {

    }

    public Order(UUID id, OrderType type, List<OrderLineItem> orderLineItems, OrderTable orderTable, DeliveryAddress deliveryAddress) {
        validateOrderLineItems(orderLineItems);
        validateType(type);
        this.type = type;
        validateOrderTable(orderTable);
        validateDeliveryAddress(deliveryAddress);
        this.status = OrderStatus.WAITING;
        this.deliveryAddress = deliveryAddress;
        this.id = id;
        this.orderDateTime = LocalDateTime.now();
        this.orderLineItems = orderLineItems;
        this.orderTable = orderTable;
    }

    private void validateDeliveryAddress(DeliveryAddress deliveryAddress) {
        if (this.type.equals(OrderType.DELIVERY) && deliveryAddress == null) {
            throw new IllegalArgumentException("배달 주문이면 배송지가 없을 수 없다.");
        }
    }

    private void validateOrderTable(OrderTable orderTable) {
        if (orderTable != null && orderTable.isOccupied()) {
            throw new IllegalArgumentException("매장 주문에서 착석된 테이블을 선택할 수 없다.");
        }
    }

    private void validateOrderLineItems(List<OrderLineItem> orderLineItems) {
        if (Objects.isNull(orderLineItems) || orderLineItems.size() == 0) {
            throw new IllegalArgumentException("주문 항목은 비어 있을 수 없습니다.");
        }
        if (this.type != OrderType.EAT_IN) {
            for (OrderLineItem orderLineItem : orderLineItems) {
                if (orderLineItem.getQuantity() < 0) {
                    throw new IllegalArgumentException("매장 주문이 아닐 경우 수량은 0개보다 적을 수 없다.");
                }
            }
        }
        for (OrderLineItem orderLineItem : orderLineItems) {
            if (!orderLineItem.getMenu().isDisplayed()) {
                throw new IllegalArgumentException("안보이는 메뉴가 주문될 수 없다.");
            }
        }
    }

    private static void validateType(OrderType type) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("주문 타입을 입력해주세요.");
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(final OrderType type) {
        this.type = type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(final LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(final List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress.getAddress();
    }

    public OrderTable getOrderTable() {
        return orderTable;
    }

    public void setOrderTable(final OrderTable orderTable) {
        this.orderTable = orderTable;
    }

    public UUID getOrderTableId() {
        return orderTableId;
    }

    public void setOrderTableId(final UUID orderTableId) {
        this.orderTableId = orderTableId;
    }

    public void accept() {
        if (this.status != OrderStatus.WAITING) {
            throw new IllegalStateException("WAITING 상태만 접수가능합니다.");
        }
        this.status = OrderStatus.ACCEPTED;
    }

    public void served() {
        if (this.status != OrderStatus.ACCEPTED) {
            throw new IllegalStateException("ACCEPTED 상태만 SERVED 상태로 변경가능합니다");
        }
        this.status = OrderStatus.SERVED;
    }

    public void delivering() {
        if (this.type != OrderType.DELIVERY) {
            throw new IllegalStateException("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있습니다.");
        }
        if (this.status != OrderStatus.SERVED) {
            throw new IllegalStateException("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.");
        }
        this.status = OrderStatus.DELIVERING;
    }

    public void completed() {
        if (this.type == OrderType.DELIVERY && this.status != OrderStatus.DELIVERING) {
            throw new IllegalStateException("주문 상태가 DELIVERING이 아니면 주문을 완료할 수 없다.");
        }
        if (this.type != OrderType.DELIVERY && this.status != OrderStatus.SERVED) {
            throw new IllegalStateException("주문 상태가 SERVED가 아니면 주문을 완료할 수 없다.");
        }
        this.status = OrderStatus.COMPLETED;
    }

    private boolean takeOut() {
        return (this.type == OrderType.TAKEOUT || this.type == OrderType.EAT_IN) && this.status == OrderStatus.SERVED;
    }

    public void delivered() {
        if (this.status != OrderStatus.DELIVERING) {
            throw new IllegalArgumentException("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void vacant() {
        if (this.status != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("주문 테이블 공석으로 변경 시 주문 상태가 완료일때만 변경 가능하다.");
        }
        this.orderTable.vacant();
    }
}
