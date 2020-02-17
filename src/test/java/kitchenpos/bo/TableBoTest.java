package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kitchenpos.model.OrderBuilder;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.OrderTableBuilder;
import kitchenpos.table.supports.TableBoFactory;

class TableBoTest {

    private static final Long EXIST_TABLE_GROUP_ID = 1L;
    private static final int MIN_NUMBER_OF_GUESTS = 0;
    private static final OrderTable CHANGEABLE_NUMBER_OF_GUESTS = OrderTableBuilder.anOrderTable()
                                                                                   .withNumberOfGuests(MIN_NUMBER_OF_GUESTS)
                                                                                   .build();

    @Test
    @DisplayName("테이블을 빈 상태로 변경한다.")
    void changeEmpty() {
        TableBo sut = TableBoFactory.withFixtures(OrderTableBuilder.anOrderTable()
                                                                   .build(),
                                                  Arrays.asList(OrderBuilder.anOrder()
                                                                            .withOrderStatus(OrderStatus.COMPLETION.name())
                                                                            .build(),
                                                                OrderBuilder.anOrder()
                                                                            .withOrderStatus(OrderStatus.COMPLETION.name())
                                                                            .build()));

        assertThat(sut.changeEmpty(TableBoFactory.EXIST_ORDER_TABLE_ID,
                                   OrderTableBuilder.anOrderTable()
                                                    .withEmpty(true)
                                                    .build()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("테이블을 빈 상태로 변경한다. 해당 테이블이 테이블 그룹에 속하지 않아야 한다.")
    void changeEmpty_when_table_is_with_table_group() {
        TableBo sut = TableBoFactory.withFixtures(OrderTableBuilder.anOrderTable()
                                                                   .withTableGroupId(EXIST_TABLE_GROUP_ID)
                                                                   .build(),
                                                  Arrays.asList(OrderBuilder.anOrder()
                                                                            .withOrderStatus(OrderStatus.COMPLETION.name())
                                                                            .build(),
                                                                OrderBuilder.anOrder()
                                                                            .withOrderStatus(OrderStatus.COMPLETION.name())
                                                                            .build()));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.changeEmpty(TableBoFactory.EXIST_ORDER_TABLE_ID,
                                              OrderTableBuilder.anOrderTable()
                                                               .withEmpty(true)
                                                               .build()));
    }

    @Test
    @DisplayName("테이블을 빈 상태로 변경한다. 해당 테이블의 주문이 조리중이나 식사중 상태가 아니어야 한다.")
    void changeEmpty_when_order_is_not_COMPLETION() {
        TableBo sut = TableBoFactory.withFixtures(OrderTableBuilder.anOrderTable()
                                                                   .build(),
                                                  Arrays.asList(OrderBuilder.anOrder()
                                                                            .withOrderStatus(OrderStatus.COMPLETION.name())
                                                                            .build(),
                                                                OrderBuilder.anOrder()
                                                                            .withOrderStatus(OrderStatus.MEAL.name())
                                                                            .build()));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.changeEmpty(TableBoFactory.EXIST_ORDER_TABLE_ID,
                                              OrderTableBuilder.anOrderTable()
                                                               .withEmpty(true)
                                                               .build()));
    }

    @Test
    @DisplayName("테이블에 있는 손님 수를 변경한다.")
    void changeNumberOfGuests() {
        TableBo sut = TableBoFactory.withFixtures(OrderTableBuilder.anOrderTable()
                                                                   .withEmpty(false)
                                                                   .build());

        OrderTable afterChangeNumberOfGuests = sut.changeNumberOfGuests(TableBoFactory.EXIST_ORDER_TABLE_ID,
                                                                        CHANGEABLE_NUMBER_OF_GUESTS);
        assertEquals(CHANGEABLE_NUMBER_OF_GUESTS.getNumberOfGuests(),
                     afterChangeNumberOfGuests.getNumberOfGuests());
    }

    @Test
    @DisplayName("테이블에 있는 손님 수를 변경한다. 테이블은 비어있는 상태가 아니어야 한다.")
    void changeNumberOfGuests_when_empty_is_true() {
        TableBo sut = TableBoFactory.withFixtures(OrderTableBuilder.anOrderTable()
                                                                   .withEmpty(true)
                                                                   .build());

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.changeNumberOfGuests(TableBoFactory.EXIST_ORDER_TABLE_ID,
                                                       CHANGEABLE_NUMBER_OF_GUESTS));
    }

    @Test
    @DisplayName("테이블에 있는 손님 수를 변경한다. 테이블에 있는 손님 수는 0명 이상이다.")
    void changeNumberOfGuests_when_numberOfGuests_is_negative() {
        TableBo sut = TableBoFactory.withFixtures(OrderTableBuilder.anOrderTable()
                                                                   .withEmpty(false)
                                                                   .build());

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.changeNumberOfGuests(TableBoFactory.EXIST_ORDER_TABLE_ID,
                                                       OrderTableBuilder.anOrderTable()
                                                                        .withNumberOfGuests(MIN_NUMBER_OF_GUESTS - 1)
                                                                        .build()));
    }
}