package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kitchenpos.model.OrderBuilder;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.OrderTableBuilder;
import kitchenpos.model.TableGroupBuilder;
import kitchenpos.table.group.supports.TableGroupBoFactory;

class TableGroupBoTest {

    @Test
    @DisplayName("테이블 그룹을 생성한다.")
    void create() {
        List<OrderTable> orderTables = Arrays.asList(OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(true)
                                                                      .withTableGroupId(null)
                                                                      .build(),
                                                     OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(true)
                                                                      .withTableGroupId(null)
                                                                      .build());
        TableGroupBo sut = TableGroupBoFactory.withFixtures(orderTables);

        sut.create(TableGroupBuilder.aTableGroup()
                                    .withOrderTables(orderTables)
                                    .build());
    }

    @Test
    @DisplayName("테이블 그룹을 생성한다. 생성 될 테이블 그룹은 테이블을 2개 이상 갖는다.")
    void create_when_number_of_tables_is_less_than_two() {
        List<OrderTable> orderTables = Arrays.asList(OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(true)
                                                                      .withTableGroupId(null)
                                                                      .build());
        TableGroupBo sut = TableGroupBoFactory.withFixtures(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(TableGroupBuilder.aTableGroup()
                                                          .withOrderTables(orderTables)
                                                          .build()));
    }

    @Test
    @DisplayName("테이블 그룹을 생성한다. 생성 될 테이블 그룹에 소속 될 테이블은 기존에 생성되어 있어야 한다.")
    void create_when_tables_are_not_exists() {
        List<OrderTable> orderTables = Collections.emptyList();
        TableGroupBo sut = TableGroupBoFactory.withFixtures(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(TableGroupBuilder.aTableGroup()
                                                          .withOrderTables(orderTables)
                                                          .build()));
    }

    @Test
    @DisplayName("테이블 그룹을 생성한다. 생성 될 테이블 그룹에 소속 될 테이블은 기존에 비어있는 상태여야한다.")
    void create_when_tables_are_not_empty() {
        List<OrderTable> orderTables = Arrays.asList(OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(false)
                                                                      .withTableGroupId(null)
                                                                      .build(),
                                                     OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(true)
                                                                      .withTableGroupId(null)
                                                                      .build());
        TableGroupBo sut = TableGroupBoFactory.withFixtures(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(TableGroupBuilder.aTableGroup()
                                                          .withOrderTables(orderTables)
                                                          .build()));
    }

    @Test
    @DisplayName("테이블 그룹을 생성한다. 생성 될 테이블 그룹에 소속 될 테이블은 기존에 임의의 테이블 그룹에 소속 되어 있지 않아야 한다.")
    void create_when_tables_are_already_in_table_group() {
        List<OrderTable> orderTables = Arrays.asList(OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(true)
                                                                      .withTableGroupId(1L)
                                                                      .build(),
                                                     OrderTableBuilder.anOrderTable()
                                                                      .withEmpty(true)
                                                                      .withTableGroupId(1L)
                                                                      .build());
        TableGroupBo sut = TableGroupBoFactory.withFixtures(orderTables);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(TableGroupBuilder.aTableGroup()
                                                          .withOrderTables(orderTables)
                                                          .build()));
    }

    @Test
    @DisplayName("테이블 그룹을 삭제한다. 삭제 될 테이블 그룹에 대한 주문은 조리중이나 식사중 상태가 아니어야 한다.")
    void delete_when_order_is_not_COMPLETION() {
        TableGroupBo sut = TableGroupBoFactory.withFixtures(TableGroupBuilder.aTableGroup()
                                                                             .build(),
                                                            OrderTableBuilder.anOrderTable()
                                                                             .build(),
                                                            Collections.singletonList(OrderBuilder.anOrder()
                                                                                                  .withOrderStatus(OrderStatus.MEAL.name())
                                                                                                  .build()));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.delete(TableGroupBoFactory.EXIST_TABLE_GROUP_ID));
    }
}