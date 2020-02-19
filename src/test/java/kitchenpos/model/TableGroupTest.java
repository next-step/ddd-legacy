package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

public class TableGroupTest {
    static final Long EMPTY_TABLE_GROUP_ID = 1L;
    static final Long TABLE_GROUP_ID = 2L;

    public static TableGroup ofTwoEmptyTable() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setId(EMPTY_TABLE_GROUP_ID);
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofEmpty(), OrderTableTest.ofAnotherEmpty())
        );
        return tableGroup;
    }

    public static TableGroup ofTwoMultiTable() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setId(TABLE_GROUP_ID);
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofFirstOfMulti(), OrderTableTest.ofSecondOfMulti())
        );
        return tableGroup;
    }
}
