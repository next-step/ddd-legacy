package kitchenpos.builder;

import kitchenpos.model.OrderTable;
import kitchenpos.model.TableGroup;

import java.time.LocalDateTime;
import java.util.List;

public class TableGroupBuilder {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTable> orderTables;

    public TableGroupBuilder() {
    }

    public TableGroupBuilder id(Long val) {
        id = val;
        return this;
    }

    public TableGroupBuilder createdDate(LocalDateTime val) {
        createdDate = val;
        return this;
    }

    public TableGroupBuilder orderTables(List<OrderTable> val) {
        orderTables = val;
        return this;
    }

    public TableGroup build() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(this.id);
        tableGroup.setCreatedDate(this.createdDate);
        tableGroup.setOrderTables(this.orderTables);
        return tableGroup;
    }
}
