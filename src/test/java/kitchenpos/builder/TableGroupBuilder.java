package kitchenpos.builder;

import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;

import java.time.LocalDateTime;
import java.util.List;

public class TableGroupBuilder {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    public TableGroupBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public TableGroupBuilder setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public TableGroupBuilder setOrderTables(List<OrderTable> orderTables) {
        this.orderTables = orderTables;
        return this;
    }

    public TableGroup build() {
        TableGroup tableGroup = new TableGroup();

        tableGroup.setCreatedDate(createdDate);
        tableGroup.setId(id);
        tableGroup.setOrderTables(orderTables);

        return tableGroup;
    }
}
