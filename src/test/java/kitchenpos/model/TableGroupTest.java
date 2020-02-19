package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

public class TableGroupTest {
    public static TableGroup ofTwoEmptyTable() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setId(1L);
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofEmpty(), OrderTableTest.ofAnotherEmpty())
        );
        return tableGroup;
    }

    public static TableGroup ofTwoMultiTable() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setId(2L);
        tableGroup.setOrderTables(
                Arrays.asList(OrderTableTest.ofFirstOfMulti(), OrderTableTest.ofSecondOfMulti())
        );
        return tableGroup;
    }

}