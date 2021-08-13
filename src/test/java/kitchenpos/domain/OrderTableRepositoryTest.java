package kitchenpos.domain;

import kitchenpos.FixtureData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableRepositoryTest extends FixtureData {

    @Mock
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        fixtureOrderTables();
    }

    @DisplayName("테이블 생성")
    @Test
    void createOrderTable() {
        OrderTable orderTable = orderTables.get(0);

        given(orderTableRepository.save(orderTable)).willReturn(orderTable);

        OrderTable createOrderTable = orderTableRepository.save(orderTable);

        assertThat(createOrderTable).isNotNull();
    }

    @DisplayName("테이블 조회")
    @Test
    void findById() {
        OrderTable orderTable = orderTables.get(0);

        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        OrderTable find = orderTableRepository.findById(orderTable.getId()).get();

        assertThat(find.getId()).isEqualTo(orderTable.getId());
    }

    @DisplayName("테이블 내역 조회")
    @Test
    void findAll() {
        given(orderTableRepository.findAll()).willReturn(orderTables);

        List<OrderTable> findAll = orderTableRepository.findAll();

        verify(orderTableRepository).findAll();
        verify(orderTableRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(orderTables.containsAll(findAll)).isTrue(),
                () -> assertThat(orderTables.size()).isEqualTo(findAll.size())
        );
    }
}