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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTests {
    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable mockOrderTable = new OrderTable();
    private final List<OrderTable> mockOrderTables = new ArrayList<>();

    @BeforeEach
    public void setup() {
        // { id: 1, empty: false, tableGroupId: 2, numberOfGuests: 3 }
        setupOrderTable();

        mockOrderTables.add(mockOrderTable);
    }

    @DisplayName("주문 테이블 생성 시도 성공")
    @Test
    public void createOrderTableSuccess() {
        given(orderTableDao.save(mockOrderTable)).willReturn(mockOrderTable);

        OrderTable orderTable = tableBo.create(mockOrderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(3);
    }

    @DisplayName("주문 테이블 전체 조회 성공")
    @Test
    public void getAllOrderTable() {
        given(orderTableDao.findAll()).willReturn(mockOrderTables);

        List<OrderTable> orderTables = tableBo.list();

        assertThat(orderTables).hasSize(1);
    }

    private void setupOrderTable() {
        mockOrderTable.setId(1L);
        mockOrderTable.setEmpty(false);
        mockOrderTable.setTableGroupId(2L);
        mockOrderTable.setNumberOfGuests(3);
    }
}
