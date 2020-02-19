package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

public class TableGroupTest {
    public static TableGroup ofTwo() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setId(1L);
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofFirstOfMulti(), OrderTableTest.ofSecondOfMulti())
        );
        return tableGroup;
    }
}