package kitchenpos.bo;

import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TableBoTest {

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo tableBo;

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable();
    }

    @Test
    @DisplayName("주문 테이블 생성")
    void create() {
        // give
        given(orderTableDao.save(orderTable))
                .willReturn(orderTable);
        OrderTable orderTableExpected = orderTable;
        // when
        OrderTable orderTableActual = tableBo.create(orderTable);
        // then
        assertThat(orderTableActual.getId()).isEqualTo(orderTableExpected.getId());
        assertThat(orderTableActual.getTableGroupId()).isEqualTo(orderTableExpected.getTableGroupId());
    }
}
