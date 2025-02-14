package kitchenpos.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "orders")
@Entity
public class Order {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Column(name = "type", nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(255)")
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

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @ManyToOne
    @JoinColumn(
        name = "order_table_id",
        columnDefinition = "binary(16)",
        foreignKey = @ForeignKey(name = "fk_orders_to_order_table")
    )
    private OrderTable orderTable;

    @Transient
    private UUID orderTableId;

    public Order() {
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
        return deliveryAddress;
    }

    public void setDeliveryAddress(final String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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

    // Builder 생성 메서드
    public static Builder builder() {
        return new Builder();
    }

    // Getter/Setter 생략 (생성 시 필요에 따라 추가)

    public static class Builder {
        private final Order order;

        public Builder() {
            order = new Order();
            order.id = UUID.randomUUID();
            order.orderLineItems = new ArrayList<>();
            order.orderDateTime = LocalDateTime.now(); // 기본값은 현재 시간
        }

        public Builder id(UUID id) {
            order.id = id;
            return this;
        }

        public Builder type(OrderType type) {
            order.type = type;
            return this;
        }

        public Builder status(OrderStatus status) {
            order.status = status;
            return this;
        }

        public Builder orderDateTime(LocalDateTime orderDateTime) {
            order.orderDateTime = orderDateTime;
            return this;
        }


        public Builder orderLineItems(List<OrderLineItem> items) {
            order.orderLineItems = items;
            return this;
        }

        public Builder deliveryAddress(String deliveryAddress) {
            order.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder orderTable(OrderTable orderTable) {
            order.orderTable = orderTable;
            order.orderTableId = orderTable.getId();
            return this;
        }

        public Builder orderTableId(UUID orderTableId) {
            order.orderTableId = orderTableId;
            return this;
        }

        public Order build() {
            // 필요한 유효성 검증 로직 추가 가능
            return order;
        }
    }
}
