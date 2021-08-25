package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    private OrderTable orderTable;
    private OrderTable sitOrderTable;

    @BeforeEach
    void setup() {
        orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("주문테이블10");
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(0);
    }

    @DisplayName("주문테이블을 등록한다.")
    @Test
    void createTest() {
        // given
        // when
        OrderTable actual = orderTableService.create(orderTable);
        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("주문테이블10");
    }

    @DisplayName("테이블 등록시 테이블명을 포함해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithoutNameTest(String name) {
        // given
        orderTable.setName(name);
        // when
        assertThatThrownBy(() -> orderTableService.create(orderTable))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 상태(사용중)에 대해 수정할 수 있다.")
    @Test
    void orderTableSitTest() {
        // given
        // when
        OrderTable actual = orderTableService.sit(UUID.fromString("8d710043-29b6-420e-8452-233f5a035520"));
        // then
        assertThat(actual.isEmpty()).isFalse();
    }

    @DisplayName("테이블 상태(사용안함)에 대해 수정할 수 있다. ")
    @Test
    void orderTableClearTest() {
        // given
        // when
        OrderTable actual = orderTableService.clear(UUID.fromString("8d710043-29b6-420e-8452-233f5a035520"));
        // then
        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual.getNumberOfGuests()).isEqualTo(0);
    }

    @DisplayName("테이블에 있는 손님의 수를 수정할때 손님 수는 0보다 적을 수 없다.")
    @ValueSource(ints = {-1, -5})
    @ParameterizedTest
    void changeNumberOfGuestsExceptionTest(int numberOfGuests) {
        // given
        OrderTable actual = orderTableRepository.findById(UUID.fromString("8d710043-29b6-420e-8452-233f5a035520"))
                .orElse(null);
        assertThat(actual).isNotNull();
        actual.setNumberOfGuests(numberOfGuests);
        // when
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(actual.getId(), actual))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비어있는 테이블의 손님 수는 수정할 수 없다.")
    @Test
    void emptyOrderTableChangeNumberOfGuestsTest() {
        // given
        OrderTable table = orderTableRepository.findById(UUID.fromString("8d710043-29b6-420e-8452-233f5a035520"))
                .orElse(null);
        assertThat(table).isNotNull();
        table.setEmpty(true);
        table.setNumberOfGuests(4);
        // when
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(table.getId(), table))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블에 있는 손님의 수를 수정할 수 있다.")
    @ValueSource(ints = {0, 3, 5})
    @ParameterizedTest
    void changeNumberOfGuests(int numberOfGuests) {
        // given
        OrderTable table = orderTableRepository.findById(UUID.fromString("8d710043-29b6-420e-8452-233f5a035520"))
                .orElse(null);
        assertThat(table).isNotNull();
        table.setEmpty(false);
        table.setNumberOfGuests(numberOfGuests);
        // when
        OrderTable actual = orderTableService.changeNumberOfGuests(table.getId(), table);
        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("테이블 목록을 조회할 수 있다.")
    @Test
    void findAllTest() {
        // given
        // when
        List<OrderTable> orderTables = orderTableService.findAll();
        // then
        assertThat(orderTables.size()).isEqualTo(8);
    }
}