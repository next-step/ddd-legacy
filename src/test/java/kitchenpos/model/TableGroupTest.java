package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

public final class TableGroupTest {
    static final Long TABLE_GROUP_ID = 1L;

    public static TableGroup of() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.of(2020, 1, 1, 12, 0));
        tableGroup.setId(TABLE_GROUP_ID);
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofFirstInTableGroup(), OrderTableTest.ofSecondInTableGroup())
        );
        return tableGroup;
    }
}
