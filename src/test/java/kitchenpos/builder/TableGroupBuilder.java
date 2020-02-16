package kitchenpos.builder;

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
        tableGroup.setId(this.id);
        tableGroup.setOrderTables(this.orderTables);
        tableGroup.setCreatedDate(this.createdDate);
        return tableGroup;
    }
}
