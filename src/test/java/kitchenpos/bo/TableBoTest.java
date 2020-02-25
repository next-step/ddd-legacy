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

import java.util.Collections;
import java.util.List;

import static kitchenpos.bo.Fixture.만석인_일번테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class TableBoTest {

    private final OrderDao orderDao = new TestOrderDao();
    private final OrderTableDao orderTableDao = new TestOrderTableDao();

    private TableBo tableBo;

    @BeforeEach
    void setUp() {
        tableBo = new TableBo(orderDao, orderTableDao);
    }

    @Nested
    @DisplayName("주문 테이블 생성 테스트")
    class TableCreateTest {
        @Test
        @DisplayName("주문 테이블을 생성 할 수 있다.")
        void create() {
            //given
            OrderTable expected = 만석인_일번테이블();

            //given - when
            OrderTable actual = tableBo.create(expected);

            //then
            Assertions.assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getTableGroupId()).isEqualTo(expected.getTableGroupId()),
                    () -> assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests())
            );
        }
    }

    @Test
    @DisplayName("전체 테이블 리스트를 조회 할 수 있다.")
    void list() {
        //given
        OrderTable expected = tableBo.create(만석인_일번테이블());

        //when
        List<OrderTable> actual = tableBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(Collections.singletonList(expected))
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

        OrderTable expected;
        expected = OrderTableBuilder
                .anOrderTable()
                .withId(createdTable.getId())
                .withNumberOfGuests(createdTable.getNumberOfGuests())
                .withTableGroupId(createdTable.getTableGroupId())
                .withEmpty(true)
                .build();

        //when
        OrderTable actual = tableBo.changeEmpty(expected.getId(), expected);

        //then
        Assertions.assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.isEmpty()).isTrue(),
                () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests()),
                () -> assertThat(actual.getTableGroupId()).isEqualTo(expected.getTableGroupId())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 100})
    @DisplayName("테이블 게스트의 수를 수정 할 수 있다.")
    void changeNumberOfGuests(int numberOfGuests) {
        //given
        OrderTable createdOrderTalbe = tableBo.create(만석인_일번테이블());

        OrderTable expected = OrderTableBuilder
                .anOrderTable()
                .withId(createdOrderTalbe.getId())
                .withTableGroupId(createdOrderTalbe.getTableGroupId())
                .withEmpty(createdOrderTalbe.isEmpty())
                .withNumberOfGuests(numberOfGuests)
                .build();

        //when
        OrderTable actual = tableBo.changeNumberOfGuests(expected.getId(), expected);

        //then
        Assertions.assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.isEmpty()).isFalse(),
                () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(numberOfGuests),
                () -> assertThat(actual.getTableGroupId()).isEqualTo(expected.getTableGroupId())
        );
    }

    @ParameterizedTest
    @DisplayName("수정 가능한 게스트의수는 0명 이상이다")
    @ValueSource(ints = {-1, -100})
    void changleNumberOfGuestException(int numberOfGuests) {
        //given
        OrderTable createdOrderTalbe = tableBo.create(만석인_일번테이블());

        OrderTable expected = OrderTableBuilder
                .anOrderTable()
                .withId(createdOrderTalbe.getId())
                .withTableGroupId(createdOrderTalbe.getTableGroupId())
                .withEmpty(createdOrderTalbe.isEmpty())
                .withNumberOfGuests(numberOfGuests)
                .build();

        //when then
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(expected.getId(), expected));
    }
}