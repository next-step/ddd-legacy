package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.List;

public class TableGroup { // 테이블그룹
    private Long id; // 테이블그룹 id
    private LocalDateTime createdDate; // 테이블그룹 생성일자
    private List<OrderTable> orderTables; // 테이블 리스트

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<OrderTable> getOrderTables() {
        return orderTables;
    }

    public void setOrderTables(final List<OrderTable> orderTables) {
        this.orderTables = orderTables;
    }
}
