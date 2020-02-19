package kitchenpos.support;

import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;

import java.time.LocalDateTime;
import java.util.List;

public class TableGroupBuilder {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    public TableGroupBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public TableGroupBuilder createdDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public TableGroupBuilder orderTables(List<OrderTable> orderTables) {
        this.orderTables = orderTables;
        return this;
    }

    public TableGroup build() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(this.id);
        tableGroup.setCreatedDate(this.createdDate);
        tableGroup.setOrderTables(this.orderTables);

        return tableGroup;
    }
}
