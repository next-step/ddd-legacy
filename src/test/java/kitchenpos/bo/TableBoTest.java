package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TestOrderDao;
import kitchenpos.dao.TestOrderTableDao;
import kitchenpos.model.OrderTable;
import kitchenpos.model.OrderTableBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static kitchenpos.bo.Fixture.만석인_일번테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class TableBoTest {

    private final OrderDao orderDao = new TestOrderDao();
    private final OrderTableDao orderTableDao = new TestOrderTableDao();

    private TableBo tableBo;

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
        orderTable = 만석인_일번테이블();
    }

    @Nested
    @DisplayName("주문 테이블 생성 테스트")
    class TableCreateTest {
        @Test
        @DisplayName("주문 테이블을 생성 할 수 있다.")
        void create() {
            //given - when
            OrderTable expected = tableBo.create(orderTable);

            //then
            Assertions.assertAll(
                    () -> assertThat(expected).isNotNull(),
                    () -> assertThat(expected.getId()).isEqualTo(orderTable.getId()),
                    () -> assertThat(expected.getTableGroupId()).isEqualTo(orderTable.getTableGroupId()),
                    () -> assertThat(expected.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests())
            );
        }
    }

    @Test
    @DisplayName("전체 테이블 리스트를 조회 할 수 있다.")
    void list() {
        //given
        OrderTable actual = tableBo.create(orderTable);

        //when
        List<OrderTable> expected = tableBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.stream().anyMatch(i -> {
                    Long expectedId = i.getId();
                    Long actualId = actual.getId();

                    return expectedId.equals(actualId);
                }))
        );
    }

    @Test
    @DisplayName("테이블의 공석 여부를 수정 할 수 있다.")
    void changeEmpty() {
        //given
        OrderTable table = OrderTableBuilder
                .anOrderTable()
                .withId(3L)
                .withNumberOfGuests(4)
                .withEmpty(false)
                .build();

        OrderTable createdTable = tableBo.create(table);

        OrderTable actual = OrderTableBuilder
                .anOrderTable()
                .withId(createdTable.getId())
                .withNumberOfGuests(createdTable.getNumberOfGuests())
                .withTableGroupId(createdTable.getTableGroupId())
                .withEmpty(true)
                .build();

        //when
        OrderTable expected = tableBo.changeEmpty(actual.getId(), actual);

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.isEmpty()).isTrue(),
                () -> assertThat(expected.getId()).isEqualTo(actual.getId()),
                () -> assertThat(expected.getNumberOfGuests()).isEqualTo(actual.getNumberOfGuests()),
                () -> assertThat(expected.getTableGroupId()).isEqualTo(actual.getTableGroupId())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 100})
    @DisplayName("테이블 게스트의 수를 수정 할 수 있다.")
    void changeNumberOfGuests(int numberOfGuests) {
        //given
        tableBo.create(orderTable);

        OrderTable actual = OrderTableBuilder
                .anOrderTable()
                .withId(orderTable.getId())
                .withTableGroupId(orderTable.getTableGroupId())
                .withEmpty(orderTable.isEmpty())
                .withNumberOfGuests(numberOfGuests)
                .build();

        //when
        OrderTable expected = tableBo.changeNumberOfGuests(orderTable.getId(), actual);

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.isEmpty()).isFalse(),
                () -> assertThat(expected.getId()).isEqualTo(actual.getId()),
                () -> assertThat(expected.getNumberOfGuests()).isEqualTo(numberOfGuests),
                () -> assertThat(expected.getTableGroupId()).isEqualTo(actual.getTableGroupId())
        );
    }

    @ParameterizedTest
    @DisplayName("수정 가능한 게스트의수는 0명 이상이다")
    @ValueSource(ints = {-1, -100})
    void changleNumberOfGuestException(int numberOfGuests) {
        //given
        tableBo.create(orderTable);

        OrderTable actual = OrderTableBuilder
                .anOrderTable()
                .withId(orderTable.getId())
                .withTableGroupId(orderTable.getTableGroupId())
                .withEmpty(orderTable.isEmpty())
                .withNumberOfGuests(numberOfGuests)
                .build();

        //when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(), actual));
    }
}
