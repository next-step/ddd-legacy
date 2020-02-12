package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable input;
    private OrderTable saved;

    @BeforeEach
    void setUp() {
        input = new OrderTable();
        input.setTableGroupId(3L);
        input.setNumberOfGuests(4);
        input.setEmpty(false);

        saved = new OrderTable();
        saved.setId(1L);
        saved.setTableGroupId(null);
        saved.setNumberOfGuests(4);
        saved.setEmpty(false);
    }

    @DisplayName("테이블 생성")
    @Test
    void create() {
        given(orderTableDao.save(input))
                .willReturn(saved);

        OrderTable result = tableBo.create(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTableGroupId()).isEqualTo(null);
        assertThat(result.getNumberOfGuests()).isEqualTo(4);
        assertThat(result.isEmpty()).isFalse();
    }

    @DisplayName("테이블 목록 조회")
    @Test
    void list() {
        given(orderTableDao.findAll())
                .willReturn(Collections.singletonList(saved));

        List<OrderTable> result = tableBo.list();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTableGroupId()).isEqualTo(null);
        assertThat(result.get(0).getNumberOfGuests()).isEqualTo(4);
        assertThat(result.get(0).isEmpty()).isFalse();
    }

    @DisplayName("존재하지 않는 테이블의 상태를 바꾸려고 했을 때 IllegalArgumentException 발생")
    @Test
    void changeEmptyWithException() {
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(1L, input));
    }

    @DisplayName("테이블 상태 바꿀 때 그룹 아이디가 있으면 IllegalArgumentException 발생")
    @Test
    void changeEmptyTableGroupId() {
        saved.setTableGroupId(3L);
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(1L, input));
    }

    @DisplayName("테이블 상태 바꿀 때 주문 상태가 조리중, 식사중이면 IllegalArgumentException 발생")
    @Test
    void changeEmptyOrderStatus() {
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                .willReturn(Boolean.TRUE);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(1L, input));
    }

    @DisplayName("테이블 상태 바꾸기 성공")
    @Test
    void changeEmpty() {
        input.setEmpty(true);
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                .willReturn(Boolean.FALSE);

        given(orderTableDao.save(saved))
                .willReturn(saved);

        OrderTable result = tableBo.changeEmpty(1L, input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTableGroupId()).isEqualTo(null);
        assertThat(result.getNumberOfGuests()).isEqualTo(4);
        assertThat(result.isEmpty()).isTrue();
    }

    @DisplayName("테이블의 손님 수 바꾸기 손님이 0보다 작으면 IllegalArgumentException 발생")
    @Test
    void changeNumberOfGuestsLessThenZero() {
        input.setNumberOfGuests(-2);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(1L, input));
    }

    @DisplayName("테이블의 손님 수 바꾸기 테이블이 존재하지 않으면 IllegalArgumentException 발생")
    @Test
    void changeNumberOfGuestsNullTable() {
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(1L, input));
    }

    @DisplayName("테이블의 손님 수 바꾸기 테이블이 비어있으면 IllegalArgumentException 발생")
    @Test
    void changeNumberOfGuestsEmptyGuests() {
        saved.setEmpty(true);
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(1L, input));
    }

    @DisplayName("테이블의 손님 수 바꾸기")
    @Test
    void changeNumberOfGuests() {
        input.setNumberOfGuests(8);
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        given(orderTableDao.save(saved))
                .willReturn(saved);

        OrderTable result = tableBo.changeNumberOfGuests(1L, input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTableGroupId()).isEqualTo(null);
        assertThat(result.getNumberOfGuests()).isEqualTo(8);
        assertThat(result.isEmpty()).isFalse();
    }
}
