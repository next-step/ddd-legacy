package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.List;

public final class TableGroupBuilder {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    private TableGroupBuilder() {}

    public static TableGroupBuilder aTableGroup() { return new TableGroupBuilder(); }

    public TableGroupBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TableGroupBuilder withCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public TableGroupBuilder withOrderTables(List<OrderTable> orderTables) {
        this.orderTables = orderTables;
        return this;
    }

    public TableGroup build() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(id);
        tableGroup.setCreatedDate(createdDate);
        tableGroup.setOrderTables(orderTables);
        return tableGroup;
    }
}
