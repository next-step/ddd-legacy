package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.support.OrderBuilder;
import kitchenpos.support.OrderTableBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TableBoTest {

    @Mock private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private static Order order;
    private static OrderTable orderTable;

    @BeforeAll
    static void setup(){
        orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(5)
            .empty(false)
            .build();

        order = new OrderBuilder()
            .id(1L)
            .orderTableId(orderTable.getId())
            .orderStatus(OrderStatus.COOKING.name())
            .orderedTime(LocalDateTime.now())
            .build();
    }

    @DisplayName("주문테이블의 id값이 설정되어있지 않으면 주문테이블을 새로 생성한다.")
    @Test
    void createWithoutID (){
        OrderTable orderTable = new OrderTableBuilder()
                .tableGroupId(1L)
                .numberOfGuests(5)
                .empty(false)
                .build();

        OrderTable indexedOrderTable = new OrderTableBuilder()
                .id(1L)
                .tableGroupId(1L)
                .numberOfGuests(5)
                .empty(false)
                .build();

        given(orderTableDao.save(orderTable)).willReturn(indexedOrderTable);

        OrderTable savedOrderTable = tableBo.create(orderTable);
        assertThat(savedOrderTable.getId()).isEqualTo(savedOrderTable.getId());
    }

    @DisplayName("주문테이블의 id값이 이미 주문테이블에 있다면, 주문테이블을 정보를 업데이트한다.")
    @Test
    void createWithID (){
        OrderTable indexedOrderTable = new OrderTableBuilder()
                .id(1L)
                .tableGroupId(1L)
                .numberOfGuests(5)
                .empty(false)
                .build();

        given(orderTableDao.save(indexedOrderTable)).willReturn(indexedOrderTable);

        OrderTable savedOrderTable = tableBo.create(indexedOrderTable);

        assertThat(savedOrderTable).isEqualToComparingFieldByField(indexedOrderTable);
    }

    @DisplayName("주문테이블의 ID를 잘 못 설정했을 때, IllegalException이 발생한다.")
    @Test
    void changeEmptyWithWrongOrderTableId(){
        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(null));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(1L, null));
    }

    @DisplayName("OrderTable에 tableGroupId가 이미 설정되어 있으면 IllegalArgumentException이 발생한다.")
    @Test
    void changeEmptyTableGroupIdAlreadySet (){
        orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(5)
            .empty(false)
            .build();

        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("요리중, 식사중일땐 테이블의 상태를 empty로 변경 할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void cannotChangeEmpty(final String status){
        orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(5)
            .empty(false)
            .build();

        order = new OrderBuilder()
            .id(1L)
            .orderTableId(orderTable.getId())
            .orderStatus(status)
            .orderedTime(LocalDateTime.now())
            .build();

        List<String> statusList = new ArrayList<>();
        statusList.add("COOKING");
        statusList.add("MEAL");

        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(), statusList)).willReturn(Boolean.TRUE);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(()-> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("OrderTable의 상태를 Null로 변경한다.")
    @Test
    void changeEmpty(){
        orderTable = new OrderTableBuilder()
            .id(1L)
            .numberOfGuests(5)
            .empty(false)
            .build();

        order = new OrderBuilder()
            .id(1L)
            .orderTableId(orderTable.getId())
            .orderStatus(OrderStatus.COMPLETION.name())
            .orderedTime(LocalDateTime.now())
            .build();

        List<String> statusList = new ArrayList<>();
        statusList.add("COMPLETITION");

        given(orderTableDao.findById(1L)).willReturn(Optional.ofNullable(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTable.getId(), statusList)).willReturn(Boolean.FALSE);

        when(orderTableDao.save(orderTable)).thenReturn(orderTable);

        OrderTable savedTable = tableBo.changeEmpty(orderTable.getId(), orderTable);

        assertThat(savedTable.isEmpty()).isEqualTo(false);
    }

    @DisplayName("손님의 수가 0이상이 아니면 IllegalArgumentException이 발생한다.")
    @Test
    void changeNumberOfGuestsNotNaturalNumber (){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(-1)
            .empty(true)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(1L, orderTable));
    }

    @DisplayName("orderTable 정보를 잘못 입력하면 IllegalArgumentException이 발생한다.")
    @Test
    void changeNumberOfGuestWithWrongOrderTableId(){
        Long orderTableId = 1L;

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.ofNullable(null));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTableId, orderTable));
    }

    @DisplayName("테이블이 비어있다면, IllegalArgumentException 이 발생한다.")
    @Test
    void changeNumberOfGeustWhenOrderTableisEmpty (){
        OrderTable wrongOrderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(5)
            .empty(true)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> tableBo.changeNumberOfGuests(wrongOrderTable.getId(),wrongOrderTable));
    }

    @DisplayName("테이블 인원을 변경한 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5})
    void changeNumberOfGuest(final int numberOfGuest){
        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .tableGroupId(1L)
            .numberOfGuests(numberOfGuest)
            .empty(false)
            .build();

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.ofNullable(orderTable));
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        assertThat(tableBo.changeNumberOfGuests(orderTable.getId(), orderTable).getNumberOfGuests()).isEqualTo(numberOfGuest);
    }
}
