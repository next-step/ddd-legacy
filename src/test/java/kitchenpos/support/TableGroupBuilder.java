package kitchenpos.support;

import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;

import java.time.LocalDateTime;
import java.util.List;

public class TableGroupBuilder {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    private TableGroupBuilder() {
    }

    public static TableGroupBuilder tableGroup() {
        return new TableGroupBuilder();
    }

    public TableGroupBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public TableGroupBuilder withCreateDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public TableGroupBuilder withOrderTables(List<OrderTable> orderTables) {
        this.orderTables = orderTables;
        return this;
    }

    public TableGroup build() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(id);
        tableGroup.setCreatedDate(createdDate);
        tableGroup.setOrderTables(orderTables);
        return tableGroup;
    }
}
